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

package me.minecraftauth.plugin.velocity;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.event.PlayerLoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class VelocityEventsListener {

    @Subscribe(order = PostOrder.FIRST, async = false)
    public void onLogin(LoginEvent event) {
        try {
            MinecraftAuthVelocity.getInstance().getService().handleLoginEvent(new PlayerLoginEvent(
                    event.getPlayer().getUniqueId(),
                    event.getPlayer().getUsername(),
                    event.getPlayer().hasPermission("minecraftauth.admin")
            ) {
                @Override
                public void disallow(String message) {
                    event.setResult(ResultedEvent.ComponentResult.denied(Component.text(message).color(NamedTextColor.RED)));
                }
            });
        } catch (LookupException e) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Unable to verify linked account").color(NamedTextColor.RED)));
            e.printStackTrace();
        }
    }

}
