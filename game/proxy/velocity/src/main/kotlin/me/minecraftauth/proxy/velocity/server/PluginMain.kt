package me.minecraftauth.proxy.velocity.server

import com.google.inject.Inject
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import me.minecraftauth.game.Common
import me.minecraftauth.game.config.GatekeeperResult
import net.kyori.adventure.text.Component
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
    private val commonAPI: Common.CommonAPI

    @Inject
    constructor(server: ProxyServer, logger: Logger, @DataDirectory dataDir: Path) {
        this.server = server
        this.logger = logger

        try {
            commonAPI = Common().init(dataDir)
        } catch (e: IllegalStateException) {
            logger.warn("Application not configured. See config.yml. Thanks!")
            throw Exception("Application not configured. See config.yml. Thanks!")
        }

        server.eventManager.register(this, this)
    }

    @Subscribe(order = PostOrder.FIRST, async = false)
    fun onJoin(e: LoginEvent) {
        val rst = commonAPI.onJoin(e.player.uniqueId)
        if (rst.type == GatekeeperResult.Type.DENIED)
            e.result = ResultedEvent.ComponentResult.denied(Component.text(rst.message ?: "Unknown MCAuth error"))
    }

    @Subscribe(order = PostOrder.FIRST, async = false)
    fun onSwitch(e: ServerPreConnectEvent) {
        val rst = commonAPI.onProxyServerSwitch(e.originalServer.serverInfo.name, e.player.uniqueId)
        if (rst.type == GatekeeperResult.Type.DENIED) {
            e.result = ServerPreConnectEvent.ServerResult.denied()
            e.player.sendMessage(Component.text(rst.message ?: "Unknown MCAuth error"))
        }
    }


}