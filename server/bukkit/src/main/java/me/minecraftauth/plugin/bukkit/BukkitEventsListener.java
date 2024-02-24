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

package me.minecraftauth.plugin.bukkit;

import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.RealmJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class BukkitEventsListener implements Listener {

    @EventHandler
    public void onPlayerLoginEvent(AsyncPlayerPreLoginEvent event) {
        try {
            boolean op = Bukkit.getOperators().stream().anyMatch(offlinePlayer -> offlinePlayer.getUniqueId().equals(event.getUniqueId()));
            MinecraftAuthBukkit.getInstance().getService().handleRealmJoinEvent(new RealmJoinEvent(event.getUniqueId(), event.getName(), op, null) {
                @Override
                public void disallow(String message) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, message);
                }
            });
        } catch (LookupException e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Unable to verify linked account");
            e.printStackTrace();
        }
    }

}
