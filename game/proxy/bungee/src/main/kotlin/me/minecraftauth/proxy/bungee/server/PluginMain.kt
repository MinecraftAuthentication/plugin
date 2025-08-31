package me.minecraftauth.proxy.bungee.server

import net.md_5.bungee.api.plugin.Plugin

class PluginMain : Plugin() {

    override fun onEnable() {
        println("Hello Bungee world!")
    }

}