package me.minecraftauth.plugin.sponge.server

import com.google.inject.Inject
import me.minecraftauth.game.Common
import me.minecraftauth.game.config.GatekeeperResult
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.Text
import java.nio.file.Path

/**
 * The main class of your Sponge plugin.
 *
 * <p>All methods are optional -- some common event registrations are included as a jumping-off point.</p>
 */
@Plugin(
    id = "mcauth",
    name = "MCAuth",
    version = "1.0",
    url = "https://minecraftauth.me",
    description = "MinecraftAuthentication allows you to link your Minecraft account to your various other accounts, such as Discord or Twitch.",
    authors = ["Scarsz", "Cam"]
)
class PluginMain @Inject constructor(
    val container: PluginContainer,
    val logger: Logger,
    @ConfigDir(sharedRoot = false) val configDir: Path
) {

    private lateinit var commonAPI: Common.CommonAPI

    @Listener
    fun onServerStarting(event: GameStartedServerEvent) {
        try {
            commonAPI = Common().init(configDir)
        } catch (e: IllegalStateException) {
            logger.warn("Application not configured. See config.yml. Thanks!")
            throw Exception("Application not configured. See config.yml. Thanks!")
        }

        Sponge.getEventManager().registerListeners(this, this)
    }

    @Listener
    fun onJoin(e: ClientConnectionEvent.Login) {
        val rst = commonAPI.onJoin(e.profile.uniqueId)
        if (rst.type == GatekeeperResult.Type.DENIED) {
            e.isCancelled = true
            e.setMessage(Text.builder(rst.message ?: "Unknown MCAuth error").build())
        }
    }
}
