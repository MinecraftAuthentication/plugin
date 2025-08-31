package me.minecraftauth.plugin.bukkit.server

import org.bukkit.plugin.java.JavaPlugin

class Bukkit : JavaPlugin() {

    override fun onEnable() {
        println("Hello Bukkit world!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
