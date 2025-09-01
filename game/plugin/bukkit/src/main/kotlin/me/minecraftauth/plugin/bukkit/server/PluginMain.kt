package me.minecraftauth.plugin.bukkit.server

import me.minecraftauth.game.Common
import me.minecraftauth.game.config.GatekeeperResult
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin

class PluginMain : JavaPlugin(), Listener {

    private lateinit var commonAPI: Common.CommonAPI

    override fun onEnable() {
        try {
            commonAPI = Common().init(dataFolder.toPath())
        } catch (e: IllegalStateException) {
            logger.warning("Application not configured. See config.yml. Thanks!")
            throw Exception("Application not configured. See config.yml. Thanks!")
        }

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        logger.info("toodles")
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(e: PlayerLoginEvent) {
        val rst = commonAPI.onJoin(e.player.uniqueId)
        if (rst.type == GatekeeperResult.Type.DENIED)
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, rst.message ?: "Unknown MCAuth error")
    }
}
