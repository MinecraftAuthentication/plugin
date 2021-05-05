package me.minecraftauth.plugin.bukkit;

import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import lombok.Getter;
import me.minecraftauth.plugin.common.service.GameService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class MinecraftAuthBukkit extends JavaPlugin {

    @Getter private static MinecraftAuthBukkit instance;
    @Getter private GameService service;

    @Override
    public void onEnable() {
        MinecraftAuthBukkit.instance = this;

        DynamicConfig config = new DynamicConfig();
        try {
            config.addSource(MinecraftAuthBukkit.class, "game-config", new File(getDataFolder(), "config.yml"));
            config.saveAllDefaults();
            config.loadAll();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            service = new GameService.Builder()
                    .withConfig(config)
                    .build();
        } catch (ParseException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new BukkitEventsListener(), this);
    }

}
