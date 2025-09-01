package me.minecraftauth.proxy.bungee.server

import me.minecraftauth.game.Common
import me.minecraftauth.game.config.GatekeeperResult
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority

class PluginMain : Plugin(), Listener {

    private lateinit var commonAPI: Common.CommonAPI

    override fun onEnable() {
        try {
            commonAPI = Common().init(dataFolder.toPath())
        } catch (e: IllegalStateException) {
            logger.warning("Application not configured. See config.yml. Thanks!")
            throw Exception("Application not configured. See config.yml. Thanks!")
        }
        proxy.pluginManager.registerListener(this, this)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(e: LoginEvent) {
        val rst = commonAPI.onJoin(e.connection.uniqueId)
        if (rst.type == GatekeeperResult.Type.DENIED)
            e.connection.disconnect(TextComponent.fromLegacy(rst.message ?: "Unknown MCAuth error"))
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onServerConnect(e: ServerConnectEvent) {
        val rst = commonAPI.onProxyServerSwitch(e.request.target.name, e.player.uniqueId)
        if (rst.type == GatekeeperResult.Type.DENIED) {
            e.isCancelled = true
            e.player.sendMessage(TextComponent.fromLegacy(rst.message ?: "Unknown MCAuth error"))
        }
    }


}