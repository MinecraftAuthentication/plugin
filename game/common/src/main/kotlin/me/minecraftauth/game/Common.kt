package me.minecraftauth.game

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings
import me.minecraftauth.game.config.GatekeeperResult
import me.minecraftauth.lib.AuthConfig
import me.minecraftauth.lib.MCAuth
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.Path
import kotlin.properties.Delegates


class Common {

    private companion object {
        lateinit var cfgPath: Path
        lateinit var cfg: YamlDocument
        var appId by Delegates.notNull<Long>()
        lateinit var appSecret: String

        lateinit var api: MCAuth
    }

    fun init(path: Path): CommonAPI {
        cfgPath = path

        cfg = YamlDocument.create(
            path.resolve("MCAuth.yml").toFile(),
            javaClass.getResourceAsStream("/MCAuth.yml") ?: throw IllegalStateException("Default configuration not found!"),
            GeneralSettings.builder()
                .setKeyFormat(GeneralSettings.KeyFormat.OBJECT)
                .build(),
            LoaderSettings.DEFAULT,
            DumperSettings.DEFAULT,
            UpdaterSettings.builder()
                .setVersioning(BasicVersioning("config-version"))
                .build()
        )

        if (cfg.getString("application.secret").isBlank() || cfg.getLong("application.id").toString().isBlank()) {
            throw IllegalStateException("Please configure your application prior to launching the server.")
        }

        api = MCAuth(
            AuthConfig(
                cfg.getLong("application.id"),
                cfg.getString("application.secret"),
                true,
                cfg.getString("debug.host"),
                cfg.getString("debug.ver")
            )
        )

        val keeper = GateKeeper(cfg, api)
        return CommonAPI(cfg, api, keeper)
    }

    class CommonAPI(private val cfg: YamlDocument, private val api: MCAuth, private val keeper: GateKeeper) {

        fun onJoin(uuid: UUID): GatekeeperResult {
            return onProxyServerSwitch("super", uuid)
        }

        fun onProxyServerSwitch(serverName: String, uuid: UUID): GatekeeperResult {
            return keeper.getServer(serverName)?.verify(uuid) ?: GatekeeperResult(GatekeeperResult.Type.DENIED, "Unable to verify login")
        }

    }

}