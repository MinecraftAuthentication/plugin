/*-
 * LICENSE
 * MinecraftAuth Plugin - Common
 * -------------
 * Copyright (C) 2021 MinecraftAuth.me
 * -------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * END
 */

package me.minecraftauth.plugin.common.feature.gatekeeper.provider;

import alexh.weak.Dynamic;
import me.minecraftauth.lib.AuthService;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.lib.account.platform.twitch.SubTier;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.feature.gatekeeper.GatekeeperFeature;

import java.util.Map;

public class TwitchSubscriptionProvider extends AbstractSubscriptionProvider {

    private final GatekeeperFeature feature;
    private final Dynamic config;

    public TwitchSubscriptionProvider(GatekeeperFeature feature, Dynamic config) {
        this.feature = feature;
        this.config = config;
    }

    public SubTier getTier() {
        if (config.is(String.class)) {
            // no specific tier given
            return SubTier.raw(1); // lowest possible sub tier value
        } else if (config.is(Map.class)) {

            int level = config.dget("Twitch").convert().intoInteger();
            if (level < 999) level *= 1000;
            return SubTier.raw(level);
        } else {
            throw new IllegalArgumentException("Invalid type for Twitch provider");
        }
    }

    @Override
    public boolean isSubscribed(MinecraftAccount account) throws LookupException {
        return AuthService.isSubscribedTwitch(getServerToken(feature, config), account.getUUID(), getTier());
    }

    @Override
    public String toString() {
        return "TwitchSubscriptionProvider(tier=" + (getTier().getValue() / 1000) + ")";
    }

}
