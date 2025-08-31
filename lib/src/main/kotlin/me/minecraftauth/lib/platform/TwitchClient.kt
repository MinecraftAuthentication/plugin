package me.minecraftauth.lib.platform

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import me.minecraftauth.lib.AuthConfig
import me.minecraftauth.lib.enum.Platform
import me.minecraftauth.lib.exception.ApplicationNotFoundException
import me.minecraftauth.lib.exception.InvalidParameterException
import me.minecraftauth.lib.exception.NoPatronsFoundException
import me.minecraftauth.lib.exception.PlatformIdNotLinkedException

class TwitchClient(
    fromPlatform: Platform,
    fromUserId: String,
    config: AuthConfig
) : BasePlatformClient(fromPlatform, fromUserId, config) {

    /**
     * Checks if the user is following the Twitch account.
     *
     * @return `true` if the user is following, `false` otherwise.
     * @throws InvalidParameterException if the request parameters are invalid.
     * @throws PlatformIdNotLinkedException if the platform ID is not linked.
     * @throws ApplicationNotFoundException if the application is not found.
     */
    fun isFollowing(): Boolean {
        val req = request<JsonObject>(
            HttpMethod.Get,
            "${fromPlatform.name.lowercase()}/$fromUserId/twitch"
        )

        val status = req.second.status
        val errMsg = req.first?.get("error")?.jsonObject?.get("message")?.toString()
        when (status) {
            HttpStatusCode.BadRequest ->
                throw InvalidParameterException(errMsg ?: "Invalid parameter")
            HttpStatusCode.Unauthorized ->
                throw PlatformIdNotLinkedException(errMsg ?: "Platform ID not linked")
            HttpStatusCode.NotFound ->
                throw ApplicationNotFoundException(errMsg ?: "Application not found")
        }
        return req.second.status == HttpStatusCode.OK
    }

    /**
     * Checks if the user is subscribed to the Twitch account.
     *
     * @return `true` if the user is subscribed, `false` otherwise.
     * @throws InvalidParameterException if the request parameters are invalid.
     * @throws PlatformIdNotLinkedException if the platform ID is not linked.
     * @throws ApplicationNotFoundException if the application is not found.
     */
    fun isSubscribed(): Boolean {
        val req = request<JsonObject>(
            HttpMethod.Get,
            "${fromPlatform.name.lowercase()}/$fromUserId/twitch/subscriber"
        )

        val status = req.second.status
        val errMsg = req.first?.get("error")?.jsonObject?.get("message")?.toString()
        when (status) {
            HttpStatusCode.BadRequest ->
                throw InvalidParameterException(errMsg ?: "Invalid parameter")
            HttpStatusCode.Unauthorized ->
                throw PlatformIdNotLinkedException(errMsg ?: "Platform ID not linked")
            HttpStatusCode.NotFound ->
                throw ApplicationNotFoundException(errMsg ?: "Application not found")
        }
        return req.second.status == HttpStatusCode.OK
    }

    /**
     * Checks if the user is subscribed to the Twitch account at a specific tier.
     *
     * @param tier The subscription tier to check.
     * @return `true` if the user is subscribed at the specified tier, `false` otherwise.
     * @throws InvalidParameterException if the request parameters are invalid.
     * @throws PlatformIdNotLinkedException if the platform ID is not linked.
     * @throws ApplicationNotFoundException if the application is not found.
     */
    fun isSubscribed(tier: String): Boolean {
        val req = request<JsonObject>(
            HttpMethod.Get,
            "${fromPlatform.name.lowercase()}/$fromUserId/twitch/subscriber/$tier"
        )

        val status = req.second.status
        val errMsg = req.first?.get("error")?.jsonObject?.get("message")?.toString()
        when (status) {
            HttpStatusCode.BadRequest ->
                throw InvalidParameterException(errMsg ?: "Invalid parameter")
            HttpStatusCode.Unauthorized ->
                throw PlatformIdNotLinkedException(errMsg ?: "Platform ID not linked")
            HttpStatusCode.NotFound ->
                throw ApplicationNotFoundException(errMsg ?: "Application not found")
        }
        return req.second.status == HttpStatusCode.OK
    }

}