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

package me.minecraftauth.plugin.common.feature.gatekeeper.function;

import com.udojava.evalex.Expression;
import me.minecraftauth.lib.AuthService;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.feature.gatekeeper.GatekeeperFeature;

import java.util.List;
import java.util.function.Supplier;

public class YouTubeMemberFunction extends AbstractFunction {

    public YouTubeMemberFunction(GatekeeperFeature gatekeeper, Supplier<MinecraftAccount> accountSupplier) {
        super(gatekeeper, "YouTubeMember", -1, accountSupplier);
    }

    @Override
    public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> lazyParams) {
        String tier = lazyParams.size() >= 1 ? lazyParams.get(0).getString() : null;

        return cache(getClass().getSimpleName(), getAccount().getUUID().toString(), tier, () -> {
            try {
                return AuthService.isMemberYouTube(getGatekeeper().getService().getServerToken(), getAccount().getUUID(), tier) ? TRUE : FALSE;
            } catch (LookupException e) {
                e.printStackTrace();
                return FALSE;
            }
        });
    }

}
