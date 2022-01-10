/*
 * Copyright 2021-2022 MinecraftAuth.me
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

package me.minecraftauth.plugin.sponge;

import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.PlayerLoginEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SpongeEventsListener {

    @Listener(order = Order.FIRST)
    public void onClientConnectionLogin(ClientConnectionEvent.Login event) {
        try {
            MinecraftAuthSponge.getInstance().getService().handleLoginEvent(new PlayerLoginEvent(
                    event.getProfile().getUniqueId(),
                    event.getProfile().getName().orElse(""),
                    false // sponge has no concept of "ops"
            ) {
                @Override
                public void disallow(String message) {
                    event.setCancelled(true);
                    event.setMessage(Text.builder(message).color(TextColors.RED));
                }
            });
        } catch (LookupException e) {
            event.setCancelled(true);
            event.setMessage(Text.builder("Unable to verify linked account").color(TextColors.RED));
            e.printStackTrace();
        }
    }

}
