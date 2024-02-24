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

package me.minecraftauth.forge.server;

import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import lombok.Getter;
import me.minecraftauth.plugin.common.service.AuthenticationService;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.server.command.ConfigCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

@Mod(MinecraftAuthMod.MOD_ID)
public class MinecraftAuthMod {

    public static final String MOD_ID = "minecraftauth";
    @Getter private static final Logger logger = LogManager.getLogger();
    @Getter private static MinecraftAuthMod instance;

    @Getter private AuthenticationService service;

    public MinecraftAuthMod() {
        MinecraftAuthMod.instance = this;
        MinecraftForge.EVENT_BUS.register(this);

        // inform mod loader that this is a server-only mod and isn't required on clients
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        DynamicConfig config = new DynamicConfig();
        try {
            config.addSource(MinecraftAuthMod.class, "game-config", new File("config", "MinecraftAuth.yml"));
            config.saveAllDefaults();
            config.loadAll();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        try {
            service = new AuthenticationService.Builder()
                    .withConfig(config)
                    .withLogger(new me.minecraftauth.plugin.common.abstracted.Logger() {
                        @Override
                        public void info(String message) {
                            logger.info(message);
                        }
                        @Override
                        public void warning(String message) {
                            logger.warn(message);
                        }
                        @Override
                        public void error(String message) {
                            logger.error(message);
                        }
                        @Override
                        public void debug(String message) {
                            logger.debug(message);
                        }
                    })
                    .build();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        new Command(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

}
