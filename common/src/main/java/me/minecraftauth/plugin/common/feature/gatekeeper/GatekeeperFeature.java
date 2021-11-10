/*
 * Copyright 2021 MinecraftAuth.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.minecraftauth.plugin.common.feature.gatekeeper;

import alexh.weak.Dynamic;
import com.udojava.evalex.AbstractOperator;
import com.udojava.evalex.Expression;
import com.udojava.evalex.Operator;
import lombok.Getter;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.plugin.common.feature.Feature;
import me.minecraftauth.plugin.common.feature.gatekeeper.function.*;
import me.minecraftauth.plugin.common.service.GameService;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class GatekeeperFeature extends Feature {

    @Getter private final GameService service;
    @Getter private final Set<Expression> expressions = new HashSet<>();
    @Getter private final Set<Operator> operators = new HashSet<>();
    @Getter private final Set<AbstractFunction> functions = new HashSet<>();
    @Getter private String kickMessage = null;

    private MinecraftAccount accountBeingEvaluated = null;
    private final ReentrantLock expressionLock = new ReentrantLock();

    public GatekeeperFeature(GameService service) {
        this.service = service;

        Supplier<MinecraftAccount> supplier = () -> accountBeingEvaluated;
        this.functions.add(new DiscordRoleFunction(this, supplier));
        this.functions.add(new DiscordServerFunction(this, supplier));
        this.functions.add(new PatreonMemberFunction(this, supplier));
        this.functions.add(new TwitchFollowerFunction(this, supplier));
        this.functions.add(new TwitchSubscriberFunction(this, supplier));
        this.functions.add(new YouTubeMemberFunction(this, supplier));
        this.functions.add(new YouTubeSubscriberFunction(this, supplier));

        this.operators.add(new AbstractOperator("and", 4, false, true) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                Objects.requireNonNull(v1, "No left boolean for AND operator");
                Objects.requireNonNull(v2, "No right boolean for AND operator");

                boolean b1 = v1.compareTo(BigDecimal.ZERO) != 0;

                if (!b1) {
                    return BigDecimal.ZERO;
                } else {
                    boolean b2 = v2.compareTo(BigDecimal.ZERO) != 0;
                    return b2 ? BigDecimal.ONE : BigDecimal.ZERO;
                }
            }
        });

        reload();
    }

    public @NotNull GatekeeperResult verify(MinecraftAccount account, boolean playerIsOp) {
        if (service.getServerToken() == null || expressions.size() == 0) return new GatekeeperResult(GatekeeperResult.Type.NOT_ENABLED);

        if (service.getConfig().getBooleanElse("Gatekeeper.OP bypass", true) && playerIsOp) {
            service.getLogger().info("[Gatekeeper] " + account + " is bypassing login requirements because they're a server operator");
            return new GatekeeperResult(GatekeeperResult.Type.BYPASSED);
        }

        if (service.getConfig().dget("Gatekeeper.Bypass").children().anyMatch(dynamic -> {
            String value = dynamic.convert().intoString();
            return value.equalsIgnoreCase(account.getUUID().toString()) || value.equalsIgnoreCase(account.getName());
        })) {
            service.getLogger().info("[Gatekeeper] " + account + " is bypassing login requirements because they're listed as a bypass player");
            return new GatekeeperResult(GatekeeperResult.Type.BYPASSED);
        }

        try {
            if (!expressionLock.tryLock(5, TimeUnit.SECONDS))
                return new GatekeeperResult(GatekeeperResult.Type.DENIED, "Unable to schedule verification, try again");
            this.accountBeingEvaluated = account;

            for (Expression expression : expressions) {
                if (expression.eval().compareTo(BigDecimal.ONE) == 0) {
                    service.getLogger().info("[Gatekeeper] " + account + " is being allowed via [" + expression.getOriginalExpression() + "]");
                    return new GatekeeperResult(GatekeeperResult.Type.ALLOWED);
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            expressionLock.unlock();
        }

        service.getLogger().info("[Gatekeeper] Denying " + account + ", no conditions were successful");
        return new GatekeeperResult(GatekeeperResult.Type.DENIED, kickMessage);
    }

    @Override
    public void reload() {
        Dynamic kickMessageDynamic = service.getConfig().dget("Gatekeeper.Kick message");
        kickMessage = kickMessageDynamic.isPresent() ? kickMessageDynamic.convert().intoString() : null;

        service.getConfig().dget("Gatekeeper.Conditions").children().forEach(d -> {
            Expression expression = new Expression(d.asString());
            for (AbstractFunction function : functions) expression.addLazyFunction(function);
            for (Operator operator : operators) expression.addOperator(operator);
            expressions.add(expression);
        });
        service.getLogger().info("[Gatekeeper] Controlling entry based on " + expressions.size() + " conditions");
    }

}
