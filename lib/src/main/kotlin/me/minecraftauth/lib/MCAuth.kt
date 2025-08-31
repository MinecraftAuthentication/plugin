@file:Suppress("unused")
package me.minecraftauth.lib

import me.minecraftauth.lib.enum.Platform
import me.minecraftauth.lib.platform.DiscordClient
import me.minecraftauth.lib.platform.PatreonClient
import me.minecraftauth.lib.platform.TwitchClient
import me.minecraftauth.lib.platform.YouTubeClient

/**
 * Configuration for MCAuth authentication.
 *
 * @property appId The application ID used for authentication.
 * @property token The authentication token.
 * @property debug Enables debug mode if true.
 * @property apiHost The base URL of the MCAuth API.
 * @property apiVersion The version of the MCAuth API to use.
 */
data class AuthConfig(
    val appId: Long,
    val token: String,
    val debug: Boolean = false,

    val apiHost: String = "https://api.minecraftauth.me",
    val apiVersion: String = "v1"
)

/**
 * Main entry point for MCAuth authentication operations.
 *
 * @property config The configuration settings for authentication.
 */
class MCAuth(private val config: AuthConfig) {

    init {
        require(config.appId.toString().isNotEmpty()) { "App ID must not be empty" }
        require(config.token.isNotEmpty()) { "Token must not be empty" }
        require(config.apiHost.isNotEmpty()) { "API Host must not be empty" }
        require(config.apiVersion.isNotEmpty()) { "API Version must not be empty" }
    }

    /**
     * Creates a PlatformSelector for the given platform and user ID.
     *
     * @param fromPlatform The source platform to authenticate from.
     * @param id The user ID on the source platform.
     * @return A PlatformSelector instance for further platform-specific operations.
     */
    fun from(fromPlatform: Platform, id: String): PlatformSelector {
        return PlatformSelector(fromPlatform, id, config)
    }

}

class PlatformSelector(private val from: Platform, private val id: String, private val config: AuthConfig) {

/**
     * Returns a DiscordClient for performing Discord-specific authentication operations.
     *
     * @return DiscordClient instance
     */
    fun discord(): DiscordClient {
        return DiscordClient(from, id, config)
    }

    /**
     * Returns a PatreonClient for performing Patreon-specific authentication operations.
     *
     * @return PatreonClient instance
     */
    fun patreon(): PatreonClient {
        return PatreonClient(from, id, config)
    }

    /**
     * Returns a TwitchClient for performing Twitch-specific authentication operations.
     *
     * @return TwitchClient instance
     */
    fun twitch(): TwitchClient {
        return TwitchClient(from, id, config)
    }

    /**
     * Returns a YouTubeClient for performing YouTube-specific authentication operations.
     *
     * @return YouTubeClient instance
     */
    fun youtube(): YouTubeClient {
        return YouTubeClient(from, id, config)
    }

}