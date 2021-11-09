/*
 * Copyright 2021 MinecraftAuth.me
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

package me.minecraftauth.plugin.common.service;

import alexh.weak.Dynamic;
import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import lombok.Getter;
import me.minecraftauth.lib.account.platform.minecraft.MinecraftAccount;
import me.minecraftauth.lib.exception.LookupException;
import me.minecraftauth.plugin.common.abstracted.Logger;
import me.minecraftauth.plugin.common.abstracted.event.PlayerLoginEvent;
import me.minecraftauth.plugin.common.feature.gatekeeper.GatekeeperFeature;
import me.minecraftauth.plugin.common.feature.gatekeeper.GatekeeperResult;

public class GameService {

    @Getter private final DynamicConfig config;
    @Getter private final Logger logger;
    @Getter private final GatekeeperFeature gatekeeperFeature;
    @Getter private String serverToken;

    private GameService(DynamicConfig config, Logger logger) {
        this.config = config;
        this.logger = logger;
        this.gatekeeperFeature = new GatekeeperFeature(this);
        reload();

        logger.info("Minecraft Authentication service ready");
    }

    public void reload() {
        Dynamic authenticationDynamic = config.dget("Authentication");
        serverToken = authenticationDynamic.isPresent() ? authenticationDynamic.convert().intoString() : null;
    }

    public void fullReload() {
        reload();
        gatekeeperFeature.reload();
    }

    public void handleLoginEvent(PlayerLoginEvent event) throws LookupException {
        GatekeeperResult gatekeeperResult = gatekeeperFeature.verify(new MinecraftAccount(event.getUuid(), event.getName()), event.isOp());

        if (gatekeeperResult.getType().willDenyLogin()) {
            event.disallow(gatekeeperResult.getMessage());
        }
    }

    public static class Builder {

        private DynamicConfig config;
        private Logger logger;

        public Builder withConfig(DynamicConfig config) {
            this.config = config;
            return this;
        }

        public Builder withLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public GameService build() throws ParseException {
            return new GameService(config, logger);
        }

    }

}
