pluginManagement {
    repositories {
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.2.0"
        id("org.jetbrains.kotlin.kapt") version "2.2.0"
        id("com.gradleup.shadow") version "9.0.2"

    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "plugin"
include("lib")

include("game")

include("game:common")

// FABRIC
include("game:mod:fabric:1.16.5")
include("game:mod:fabric:1.18.2")
include("game:mod:fabric:1.19.2")

// FORGE
include("game:mod:forge:1.16.5")
include("game:mod:forge:1.18.2")
include("game:mod:forge:1.19.2")

// NEOFORGE
include("game:mod:neo:1.21.1")

// PROXY
include("game:proxy:velocity")
include("game:proxy:bungee")

// PLUGIN
include("game:plugin:bukkit")
include("game:plugin:sponge")
include("game:plugin:paper")