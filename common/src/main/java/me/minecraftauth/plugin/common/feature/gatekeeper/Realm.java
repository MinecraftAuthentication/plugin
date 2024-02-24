/*
 * Copyright 2021-2024 MinecraftAuth.me
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
import com.udojava.evalex.Operator;
import lombok.Getter;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.plugin.common.feature.gatekeeper.function.AbstractFunction;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Realm {

    @Getter private final GatekeeperFeature gatekeeper;
    @Getter private final String server;
    @Getter private final String kickMessage;
    @Getter private final List<Expression> expressions = new LinkedList<>();

    protected Realm(GatekeeperFeature gatekeeper, Dynamic config, String server) {
        this.gatekeeper = gatekeeper;
        this.server = server;

        Dynamic _kickMessage = config.get("Kick message");
        this.kickMessage = _kickMessage.isPresent() ? _kickMessage.convert().intoString() : null;

        config.get("Conditions").children().forEach(d -> {
            Expression expression = new Expression(d.asString());
            for (AbstractFunction function : gatekeeper.getFunctions()) expression.addLazyFunction(function);
            for (Operator operator : gatekeeper.getOperators()) expression.addOperator(operator);
            expressions.add(expression);
        });
    }

    public GatekeeperResult verify(MinecraftAccount account) {
        boolean first = true;
        for (Expression expression : expressions) {
            if (expression.eval().compareTo(BigDecimal.ONE) == 0) {
                gatekeeper.getService().getLogger().info("[Gatekeeper] " + account + (server != null ? "@" + server : "") + " is being allowed via [" + expression.getOriginalExpression() + "]");
                expression.incrementSuccessCount();
                if (!first) expressions.sort(Comparator.comparingInt(value -> -value.successCount));
                return new GatekeeperResult(GatekeeperResult.Type.ALLOWED);
            }
            first = false;
        }
        return new GatekeeperResult(GatekeeperResult.Type.DENIED, kickMessage);
    }

}
