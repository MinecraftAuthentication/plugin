import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow")
    id("org.spongepowered.gradle.plugin") version "2.2.0"
}

group = "me.minecraftauth.plugin"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/") {
        name = "spongepowered-repo"
    }
}

dependencies {
    implementation(project(path = ":game:common", configuration = "shadow"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

sponge {
    apiVersion("7.3.0")
    license("All-Rights-Reserved")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("sponge") {
        displayName("MCAuth")
        entrypoint("me.minecraftauth.plugin.sponge.PluginMain")
        description("MinecraftAuthentication allows you to link your Minecraft account to your various other accounts, such as Discord or Twitch.")
        links {
            homepage("https://minecraftauth.me")
            source("https://github.com/MinecraftAuthentication/plugin")
            issues("https://discord.gg/QhBWpURsD7")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

tasks.shadowJar {
    mergeServiceFiles()
    minimize()
    archiveClassifier.set("")
    archiveFileName.set("MCAuth-Sponge-${project.version}.jar")

    doLast {
        val targetDir = file("$rootDir/.libs/plugin")
        targetDir.mkdirs()
        val outputFile = archiveFile.get().asFile
        copy {
            from(outputFile)
            into(targetDir)
        }
    }
}

tasks.build {
    dependsOn("shadowJar")
}

val javaTarget = 21 // Sponge targets a minimum of Java 21
kotlin {
    jvmToolchain(javaTarget)
}

// Make sure all tasks which produce archives (jar, sources jar, javadoc jar, etc) produce more consistent output
tasks.withType<AbstractArchiveTask>().configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}
