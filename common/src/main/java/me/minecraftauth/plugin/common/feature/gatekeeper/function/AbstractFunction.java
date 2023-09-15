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

package me.minecraftauth.plugin.common.feature.gatekeeper.function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.udojava.evalex.AbstractLazyFunction;
import com.udojava.evalex.Expression;
import lombok.Getter;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.plugin.common.feature.gatekeeper.GatekeeperFeature;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractFunction extends AbstractLazyFunction {

    static final Cache<String, Expression.LazyNumber> VALUE_CACHE = Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).build();

    public static final Expression.LazyNumber TRUE = new Expression.LazyNumber() {
        @Override
        public BigDecimal eval() {
            return BigDecimal.ONE;
        }

        @Override
        public String getString() {
            return "1";
        }
    };

    public static final Expression.LazyNumber FALSE = new Expression.LazyNumber() {
        @Override
        public BigDecimal eval() {
            return BigDecimal.ZERO;
        }

        @Override
        public String getString() {
            return "0";
        }
    };

    @Getter private final GatekeeperFeature gatekeeper;
    private final Supplier<MinecraftAccount> accountSupplier;

    public AbstractFunction(GatekeeperFeature gatekeeper, String name, int numParams, Supplier<MinecraftAccount> accountSupplier) {
        super(name, numParams, true);
        this.gatekeeper = gatekeeper;
        this.accountSupplier = accountSupplier;
    }

    public MinecraftAccount getAccount() {
        return accountSupplier.get();
    }

}
