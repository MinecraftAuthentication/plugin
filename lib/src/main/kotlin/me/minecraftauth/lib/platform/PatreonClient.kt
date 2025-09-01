package me.minecraftauth.lib.platform

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import me.minecraftauth.lib.AuthConfig
import me.minecraftauth.lib.data.Platform
import me.minecraftauth.lib.exception.ApplicationNotFoundException
import me.minecraftauth.lib.exception.InvalidParameterException
import me.minecraftauth.lib.exception.NoPatronsFoundException
import me.minecraftauth.lib.exception.NoTierFoundException
import me.minecraftauth.lib.exception.PlatformIdNotLinkedException

class PatreonClient(
    fromPlatform: Platform,
    fromUserId: String,
    config: AuthConfig
) : BasePlatformClient(fromPlatform, fromUserId, config) {

    /**
     * Checks if the user is a Patreon member.
     *
     * @return `true` if the user is a Patreon member, `false` otherwise.
     * @throws InvalidParameterException if the request parameters are invalid.
     * @throws PlatformIdNotLinkedException if the platform ID is not linked.
     * @throws NoPatronsFoundException if no patrons are found for the application owner.
     * @throws ApplicationNotFoundException if the application is not found.
     */
    fun isMember(): Boolean {
        val req = request<JsonObject>(
            HttpMethod.Get,
            "${fromPlatform.name.lowercase()}/$fromUserId/patreon"
        )

        val status = req.second.status
        val errMsg = req.first?.get("error")?.jsonObject?.get("message")?.toString()
        when (status) {
            HttpStatusCode.BadRequest ->
                throw InvalidParameterException(errMsg ?: "Invalid parameter")
            HttpStatusCode.Unauthorized ->
                throw PlatformIdNotLinkedException(errMsg ?: "Platform ID not linked")
            HttpStatusCode.NotFound -> {
                when (req.first?.get("error")?.jsonObject["code"]?.toString()?.trim('"')) {
                    "no_patrons_found" -> throw NoPatronsFoundException(errMsg ?: "Guilds not found")
                    "application_not_found" -> throw ApplicationNotFoundException(errMsg ?: "Application not found")
                    else -> throw InvalidParameterException(errMsg ?: "Unknown error")
                }
            }
        }
        return req.second.status == HttpStatusCode.OK
    }

    /**
     * Checks if the user is a Patreon member of a specific tier.
     *
     * @param tierName The name of the Patreon tier to check membership for.
     * @return `true` if the user is a member of the specified tier, `false` otherwise.
     * @throws InvalidParameterException if the request parameters are invalid.
     * @throws PlatformIdNotLinkedException if the platform ID is not linked.
     * @throws NoPatronsFoundException if no patrons are found for the application owner.
     * @throws NoTierFoundException if the specified tier is not found.
     * @throws ApplicationNotFoundException if the application is not found.
     */
    fun isMember(tierName: String): Boolean {
        val req = request<JsonObject>(
            HttpMethod.Get,
            "${fromPlatform.name.lowercase()}/$fromUserId/patreon/tier/$tierName"
        )

        val status = req.second.status
        val errMsg = req.first?.get("error")?.jsonObject?.get("message")?.toString()
        when (status) {
            HttpStatusCode.BadRequest ->
                throw InvalidParameterException(errMsg ?: "Invalid parameter")
            HttpStatusCode.Unauthorized ->
                throw PlatformIdNotLinkedException(errMsg ?: "Platform ID not linked")
            HttpStatusCode.NotFound -> {
                when (req.first?.get("error")?.jsonObject["code"]?.toString()?.trim('"')) {
                    "no_patrons_found" -> throw NoPatronsFoundException(errMsg ?: "Guilds not found")
                    "no_tier_found" -> throw NoTierFoundException(errMsg ?: "No tier found")
                    "application_not_found" -> throw ApplicationNotFoundException(errMsg ?: "Application not found")
                    else -> throw InvalidParameterException(errMsg ?: "Unknown error")
                }
            }
        }
        return req.second.status == HttpStatusCode.OK
    }

}