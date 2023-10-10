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

package me.minecraftauth.plugin.bungee;

import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import lombok.Getter;
import me.minecraftauth.plugin.common.service.AuthenticationService;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public final class MinecraftAuthBungee extends Plugin {

    @Getter private static MinecraftAuthBungee instance;
    @Getter private AuthenticationService service;

    @Override
    public void onEnable() {
        MinecraftAuthBungee.instance = this;

        DynamicConfig config = new DynamicConfig();
        try {
            config.addSource(MinecraftAuthBungee.class, "game-config", new File(getDataFolder(), "config.yml"));
            config.saveAllDefaults();
            config.loadAll();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        try {
            service = new AuthenticationService.Builder()
                    .withConfig(config)
                    .withLogger(new BungeeLogger(config, getLogger()))
                    .build();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeEventsListener());
    }

}
