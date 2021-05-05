package me.minecraftauth.plugin.sponge;

import com.google.inject.Inject;
import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import lombok.Getter;
import me.minecraftauth.plugin.common.service.GameService;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "minecraftauth",
        name = "MinecraftAuth",
        version = "1.0-SNAPSHOT",
        description = "Authenticate players in your Minecraft server to various services",
        url = "https://minecraftauth.me",
        authors = {
                "MinecraftAuth"
        }
)
public class MinecraftAuthSponge {

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path sharedConfigDir;

    @Inject
    private Logger logger;

    @Getter private static MinecraftAuthSponge instance;
    @Getter private GameService service;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        MinecraftAuthSponge.instance = this;

        DynamicConfig config = new DynamicConfig();
        try {
            config.addSource(MinecraftAuthSponge.class, "game-config", new File(sharedConfigDir.toFile(), "MinecraftAuth.yml"));
            config.saveAllDefaults();
            config.loadAll();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        try {
            service = new GameService.Builder()
                    .withConfig(config)
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        Sponge.getEventManager().registerListeners(this, new SpongeEventsListener());
    }

}
