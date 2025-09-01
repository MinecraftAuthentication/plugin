package me.minecraftauth.game

import github.scarsz.configuralize.DynamicConfig
import me.minecraftauth.game.config.GatekeeperResult
import me.minecraftauth.lib.AuthConfig
import me.minecraftauth.lib.MCAuth
import java.io.File
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.Path
import kotlin.properties.Delegates


class Common {

    private companion object {
        lateinit var cfgPath: Path
        lateinit var cfg: DynamicConfig
        var appId by Delegates.notNull<Long>()
        lateinit var appSecret: String

        lateinit var api: MCAuth
    }

    fun init(path: Path): CommonAPI {
        cfgPath = path

        cfg = DynamicConfig()
        cfg.addSource(CommonAPI::class.java, "config", Path(path.toFile().absolutePath, "config.yml").toFile())

        cfg.saveAllDefaults()
        cfg.loadAll()

        if (cfg.getString("application.secret").isBlank() || cfg.getLong("application.id").toString().isBlank()) {
            throw IllegalStateException("Please configure your application prior to launching the server.")
        }

        api = MCAuth(
            AuthConfig(
                appId = cfg.getLong("application.id"),
                token = cfg.getString("application.secret"),
                debug = true
            )
        )

        val keeper = GateKeeper(cfg, api)
        return CommonAPI(cfg, api, keeper)
    }

    class CommonAPI(private val cfg: DynamicConfig, private val api: MCAuth, private val keeper: GateKeeper) {

        fun onJoin(uuid: UUID): GatekeeperResult {
            return onProxyServerSwitch("super", uuid)
        }

        fun onProxyServerSwitch(serverName: String, uuid: UUID): GatekeeperResult {
            return keeper.getServer(serverName)?.verify(uuid) ?: GatekeeperResult(GatekeeperResult.Type.DENIED, "Unable to verify login")
        }

    }

}