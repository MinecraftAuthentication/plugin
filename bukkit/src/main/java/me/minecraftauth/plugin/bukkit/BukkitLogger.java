package me.minecraftauth.plugin.bukkit;

import github.scarsz.configuralize.DynamicConfig;
import me.minecraftauth.plugin.common.abstracted.Logger;

public class BukkitLogger implements Logger {

    private final DynamicConfig config;
    private final java.util.logging.Logger logger;

    public BukkitLogger(DynamicConfig config, java.util.logging.Logger logger) {
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
