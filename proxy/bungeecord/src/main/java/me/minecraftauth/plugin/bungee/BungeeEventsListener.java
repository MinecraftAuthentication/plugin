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

package me.minecraftauth.plugin.bungee;

import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.RealmJoinEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeEventsListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(LoginEvent event) {
        try {
            boolean admin = event.getConnection() instanceof ProxiedPlayer && ((ProxiedPlayer) event.getConnection()).getGroups().contains("admin");
            MinecraftAuthBungee.getInstance().getService().handleRealmJoinEvent(new RealmJoinEvent(event.getConnection().getUniqueId(), event.getConnection().getName(), admin, null) {
                @Override
                public void disallow(String message) {
                    event.setCancelled(true);
                    event.setCancelReason(TextComponent.fromLegacyText(message));
                }
            });
        } catch (LookupException e) {
            event.setCancelled(true);
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "Unable to verify linked account"));
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerConnect(ServerConnectEvent event) {
        try {
            boolean admin = event.getPlayer().getGroups().contains("admin");
            MinecraftAuthBungee.getInstance().getService().handleRealmJoinEvent(new RealmJoinEvent(event.getPlayer().getUniqueId(), event.getPlayer().getName(), admin, null) {
                @Override
                public void disallow(String message) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(TextComponent.fromLegacyText(message));
                }
            });
        } catch (LookupException e) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unable to verify linked account"));
            e.printStackTrace();
        }
    }

}
