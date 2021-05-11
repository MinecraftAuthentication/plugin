/*-
 * LICENSE
 * MinecraftAuth Plugin - Bukkit
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

package me.minecraftauth.plugin.bukkit;

import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import lombok.Getter;
import me.minecraftauth.plugin.common.service.GameService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class MinecraftAuthBukkit extends JavaPlugin {

    @Getter private static MinecraftAuthBukkit instance;
    @Getter private GameService service;

    @Override
    public void onEnable() {
        MinecraftAuthBukkit.instance = this;

        DynamicConfig config = new DynamicConfig();
        try {
            config.addSource(MinecraftAuthBukkit.class, "game-config", new File(getDataFolder(), "config.yml"));
            config.saveAllDefaults();
            config.loadAll();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            service = new GameService.Builder()
                    .withConfig(config)
                    .withLogger(new BukkitLogger(config, getLogger()))
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new BukkitEventsListener(), this);
    }

}
