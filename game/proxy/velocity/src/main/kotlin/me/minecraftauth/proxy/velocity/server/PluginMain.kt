package me.minecraftauth.proxy.velocity.server

import com.google.inject.Inject
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import me.minecraftauth.game.Common
import org.slf4j.Logger
import java.nio.file.Path

@Plugin(
    id = "mcauth",
    name = "MCAuth",
    version = "1.0",
    url = "https://minecraftauth.me",
    authors = ["Scarsz", "Cam"],
    description = "MinecraftAuthentication allows you to link your Minecraft account to your various other accounts, such as Discord or Twitch."
)
class PluginMain {

    private val server: ProxyServer
    private val logger: Logger

    @Inject
    constructor(server: ProxyServer, logger: Logger, @DataDirectory dataDir: Path) {
        this.server = server
        this.logger = logger

        logger.info("Hello Velocity world!")

        try {
            Common().init(dataDir)
        } catch (e: IllegalStateException) {
            logger.warn("Application not configured. See config.yml. Thanks!")
            throw Exception("Disabling plugin.")
        }
    }


}