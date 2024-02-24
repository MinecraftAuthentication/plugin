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

package me.minecraftauth.plugin.sponge;

import com.google.inject.Inject;
import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import lombok.Getter;
import me.minecraftauth.plugin.common.service.AuthenticationService;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "minecraftauth",
        name = "MinecraftAuth",
        url = "https://minecraftauth.me",
        description = "Authenticate players in your Minecraft server to various services",
        authors = {"MinecraftAuth"}
)
public class MinecraftAuthSponge {

    @Getter private static MinecraftAuthSponge instance;
    @Getter private AuthenticationService service;

    @Inject private Logger logger;
    @Inject @ConfigDir(sharedRoot = true) private Path sharedConfigDir;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        MinecraftAuthSponge.instance = this;

        DynamicConfig config = new DynamicConfig();
        try {
            config.addSource(MinecraftAuthSponge.class, "game-config", new File(sharedConfigDir.toFile(), "MinecraftAuth.yml"));
            config.saveAllDefaults();
            config.loadAll();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        try {
            service = new AuthenticationService.Builder()
                    .withConfig(config)
                    .withLogger(new SpongeLogger(config, logger))
                    .build();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        Sponge.getEventManager().registerListeners(this, new SpongeEventsListener());
    }

}
