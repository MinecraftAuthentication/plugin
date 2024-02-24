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

import github.scarsz.configuralize.DynamicConfig;
import me.minecraftauth.plugin.common.abstracted.Logger;

public class BungeeLogger implements Logger {

    private final DynamicConfig config;
    private final java.util.logging.Logger logger;

    public BungeeLogger(DynamicConfig config, java.util.logging.Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warning(String message) {
        logger.warning(message);
    }

    @Override
    public void error(String message) {
        logger.severe(message);
    }

    @Override
    public void debug(String message) {
        if (config.getBooleanElse("Debug", false)) {
            info("[DEBUG] " + message);
        }
    }

}
