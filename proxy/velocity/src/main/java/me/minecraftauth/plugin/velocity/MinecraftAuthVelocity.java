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

package me.minecraftauth.plugin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import lombok.Getter;
import me.minecraftauth.plugin.common.service.AuthenticationService;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "minecraftauth",
        name = "Minecraft Authentication",
        url = "https://minecraftauth.me",
        description = "Authenticate players in your Minecraft server to various services",
        authors = {"MinecraftAuth"}
)
public class MinecraftAuthVelocity {

    @Getter private static MinecraftAuthVelocity instance;
    @Getter private AuthenticationService service;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public MinecraftAuthVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        MinecraftAuthVelocity.instance = this;

        DynamicConfig config = new DynamicConfig();
        try {
            config.addSource(MinecraftAuthVelocity.class, "proxy-config", new File(dataDirectory.toFile(), "MinecraftAuth.yml"));
            config.saveAllDefaults();
            config.loadAll();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        try {
            service = new AuthenticationService.Builder()
                    .withConfig(config)
                    .withLogger(new VelocityLogger(config, logger))
                    .build();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        server.getEventManager().register(this, new VelocityEventsListener());
    }

}
