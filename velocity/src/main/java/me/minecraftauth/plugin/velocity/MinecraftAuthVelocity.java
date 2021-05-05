package me.minecraftauth.plugin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "plugin-velocity",
        name = "Plugin",
        version = "1.0-SNAPSHOT",
        description = "Authenticate players in your Minecraft server to various services",
        url = "https://minecraftauth.me",
        authors = {"MinecraftAuth"}
)
public class MinecraftAuthVelocity {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

    }

}
