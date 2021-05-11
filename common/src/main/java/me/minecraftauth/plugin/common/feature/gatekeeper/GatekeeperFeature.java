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

package me.minecraftauth.plugin.common.feature.gatekeeper;

import alexh.weak.Dynamic;
import lombok.Getter;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.feature.Feature;
import me.minecraftauth.plugin.common.feature.gatekeeper.provider.*;
import me.minecraftauth.plugin.common.service.GameService;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class GatekeeperFeature extends Feature {

    @Getter private final GameService service;
    @Getter private final Set<MembershipProvider> providers = new HashSet<>();
    @Getter private String kickMessage = null;

    public GatekeeperFeature(GameService service) {
        this.service = service;
        reload();
    }

    public @NotNull GatekeeperResult verify(MinecraftAccount account) throws LookupException {
        if (service.getServerToken() == null || providers.size() == 0) return new GatekeeperResult(GatekeeperResult.Type.NOT_ENABLED);
        boolean subscribed = false;
        for (MembershipProvider provider : providers) {
            if (provider.isSubscribed(account)) {
                service.getLogger().info("Minecraft account " + account.getUUID() + " is a member via [" + provider + "]");
                subscribed = true;
                break;
            }
        }

        if (subscribed) {
            return new GatekeeperResult(GatekeeperResult.Type.ALLOWED);
        } else {
            service.getLogger().info("Denying Minecraft account " + account.getUUID() + ", no providers were successful");
            return new GatekeeperResult(GatekeeperResult.Type.DENIED, kickMessage);
        }
    }

    @Override
    public void reload() {
        Dynamic kickMessageDynamic = service.getConfig().dget("Gatekeeper.Kick message");
        kickMessage = kickMessageDynamic.isPresent() ? kickMessageDynamic.convert().intoString() : null;

        Dynamic providersDynamic = service.getConfig().dget("Gatekeeper.Providers");
        providersDynamic.children().forEach(providerDynamic -> {
            MembershipProvider provider;

            if ((providerDynamic.is(String.class) && providerDynamic.asString().equalsIgnoreCase("discord"))
                    || providerDynamic.dget("Discord").isPresent()) {
                if (providerDynamic.isMap() && providerDynamic.get("Discord").convert().intoString().toLowerCase().startsWith("in")) {
                    provider = new DiscordMemberPresentProvider(this, providerDynamic);
                } else {
                    provider = new DiscordRolePresentProvider(this, providerDynamic);
                }
            } else if ((providerDynamic.is(String.class) && providerDynamic.asString().equalsIgnoreCase("patreon"))
                    || providerDynamic.dget("Patreon").isPresent()) {
                provider = new PatreonSubscriptionProvider(this, providerDynamic);
            } else if ((providerDynamic.is(String.class) && providerDynamic.asString().equalsIgnoreCase("twitch"))
                    || providerDynamic.dget("Twitch").isPresent()) {
                if (providerDynamic.isMap() && providerDynamic.dget("Twitch").convert().intoString().toLowerCase().startsWith("follow")) {
                    provider = new TwitchFollowerProvider(this, providerDynamic);
                } else {
                    provider = new TwitchSubscriptionProvider(this, providerDynamic);
                }
            } else if ((providerDynamic.is(String.class) && providerDynamic.asString().equalsIgnoreCase("youtube"))
                    || providerDynamic.dget("YouTube").isPresent()) {
                if (providerDynamic.isMap() && providerDynamic.dget("YouTube").asString().toLowerCase().startsWith("sub")) {
                    provider = new YouTubeSubscriberProvider(this, providerDynamic);
                } else {
                    provider = new YouTubeMemberProvider(this, providerDynamic);
                }
            } else {
                throw new IllegalArgumentException("Unknown provider config " + providerDynamic.key().convert().intoString());
            }

            providers.add(provider);
        });
        service.getLogger().info("Utilizing " + providers.size() + " gatekeeper providers");
    }

}
