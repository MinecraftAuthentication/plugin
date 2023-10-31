/*
 * Copyright 2021-2023 MinecraftAuth.me
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

import com.udojava.evalex.AbstractOperator;
import com.udojava.evalex.Operator;
import lombok.Getter;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.plugin.common.feature.Feature;
import me.minecraftauth.plugin.common.feature.gatekeeper.function.*;
import me.minecraftauth.plugin.common.service.AuthenticationService;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class GatekeeperFeature extends Feature {

    @Getter private final AuthenticationService service;
    @Getter private final Map<String, Realm> realms = new HashMap<>();

    @Getter private final Set<AbstractFunction> functions = new HashSet<>();
    @Getter private final Set<Operator> operators = new HashSet<>();

    private final ReentrantLock expressionLock = new ReentrantLock();
    private MinecraftAccount accountBeingEvaluated = null;

    public GatekeeperFeature(AuthenticationService service) {
        this.service = service;

        Supplier<MinecraftAccount> supplier = () -> accountBeingEvaluated;
        this.functions.add(new DiscordRoleFunction(this, supplier));
        this.functions.add(new DiscordServerFunction(this, supplier));
        this.functions.add(new GlimpseSponsorFunction(this, supplier));
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

    public @NotNull GatekeeperResult verify(MinecraftAccount account, boolean playerIsAdmin) {
        return verify(account, playerIsAdmin, null);
    }
    public @NotNull GatekeeperResult verify(MinecraftAccount account, boolean playerIsAdmin, String server) {
        if (service.getServerToken() == null || realms.isEmpty()) return new GatekeeperResult(GatekeeperResult.Type.NOT_ENABLED);

        if (playerIsAdmin && service.getConfig().getBooleanElse("Gatekeeper.Admin bypass", service.getConfig().getBooleanElse("Gatekeeper.OP bypass", true))) {
            service.getLogger().info("[Gatekeeper] " + account + " is bypassing login requirements because they're a server admin");
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

            Realm realm = realms.get(server);
            if (realm != null) {
                GatekeeperResult result = realm.verify(account);
                if (result.getType() == GatekeeperResult.Type.DENIED) {
                    service.getLogger().info("[Gatekeeper] Denying " + account + (server != null ? "@" + server : "") + ", no conditions were successful");
                }
                return result;
            } else {
                return new GatekeeperResult(GatekeeperResult.Type.NOT_ENABLED);
            }
        } catch (InterruptedException e) {
            service.getLogger().info("[Gatekeeper] Denying " + account + ", verification was interrupted");
            return new GatekeeperResult(GatekeeperResult.Type.DENIED, "Verification was interrupted, try again");
        } finally {
            expressionLock.unlock();
        }
    }

    @Override
    public void reload() {
        realms.clear();

        Realm superRealm = new Realm(this, service.getConfig().dget("Gatekeeper"), null);
        if (!superRealm.getExpressions().isEmpty()) {
            realms.put(null, superRealm);
        }

        service.getConfig().dget("Gatekeeper.Servers").children().forEach(child -> {
            String server = child.key().convert().intoString();
            realms.put(server, new Realm(this, child, server));
        });

        boolean onlySuper = realms.keySet().stream().allMatch(Objects::isNull);
        int expressionCount = realms.values().stream().mapToInt(realm -> realm.getExpressions().size()).sum();

        service.getLogger().info(new StringBuilder()
                .append("[Gatekeeper] Controlling entry ")
                .append(!onlySuper ? "to " + realms.size() + " realm" + (realms.size() > 1 ? "s" : "") + ", " : "")
                .append("based on ").append(expressionCount).append(" conditions")
                .toString()
        );
    }

}
