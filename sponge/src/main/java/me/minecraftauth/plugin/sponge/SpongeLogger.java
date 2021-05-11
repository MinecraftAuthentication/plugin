package me.minecraftauth.plugin.sponge;

import github.scarsz.configuralize.DynamicConfig;
import me.minecraftauth.plugin.common.abstracted.Logger;

public class SpongeLogger implements Logger {

    private final DynamicConfig config;
    private final org.slf4j.Logger logger;

    public SpongeLogger(DynamicConfig config, org.slf4j.Logger logger) {
        this.config = config;
        this.logger = logger;
    }

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
        if (config.getBooleanElse("Debug", false)) {
            info("[DEBUG] " + message);
        }
    }

}
