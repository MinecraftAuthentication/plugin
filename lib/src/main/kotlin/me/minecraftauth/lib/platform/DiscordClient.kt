package me.minecraftauth.lib.platform

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import me.minecraftauth.lib.AuthConfig
import me.minecraftauth.lib.enum.Platform
import me.minecraftauth.lib.exception.ApplicationNotFoundException
import me.minecraftauth.lib.exception.GuildNotAuthorizedException
import me.minecraftauth.lib.exception.GuildPermissionsNotFoundException
import me.minecraftauth.lib.exception.InvalidParameterException
import me.minecraftauth.lib.exception.PlatformIdNotLinkedException

class DiscordClient(
    fromPlatform: Platform,
    fromUserId: String,
    config: AuthConfig
) : BasePlatformClient(fromPlatform, fromUserId, config) {

    /**
     * Checks if the user is a member of the specified Discord guild.
     *
     * @param guildId The ID of the Discord guild to check membership for.
     * @return `true` if the user is in the guild, `false` otherwise.
     * @throws InvalidParameterException If the request parameters are invalid.
     * @throws PlatformIdNotLinkedException If the user's platform ID is not linked.
     * @throws GuildNotAuthorizedException If the application owner is not authorized for the guild.
     * @throws ApplicationNotFoundException If the application is not found.
     * @throws GuildPermissionsNotFoundException If the guild permissions are not found.
     */
    fun isInGuild(guildId: Long): Boolean {
        val req = request<JsonObject>(
            HttpMethod.Get,
            "${fromPlatform.name.lowercase()}/$fromUserId/discord/$guildId"
        )

        val status = req.second.status
        val errMsg = req.first?.get("error")?.jsonObject?.get("message")?.toString()
        when (status) {
            HttpStatusCode.BadRequest ->
                throw InvalidParameterException(errMsg ?: "Invalid parameter")
            HttpStatusCode.Unauthorized ->
                throw PlatformIdNotLinkedException(errMsg ?: "Platform ID not linked")
            HttpStatusCode.Forbidden ->
                throw GuildNotAuthorizedException(errMsg ?: "Guild not authorized")
            HttpStatusCode.NotFound -> {
                when (req.first?.get("error")?.jsonObject["code"]?.toString()?.trim('"')) {
                    "guilds_not_found" -> throw GuildNotAuthorizedException(errMsg ?: "Guilds not found")
                    "application_not_found" -> throw ApplicationNotFoundException(errMsg ?: "Application not found")
                    "guild_permissions_not_found" -> throw GuildPermissionsNotFoundException(errMsg ?: "Guild permissions not found")
                    else -> throw InvalidParameterException(errMsg ?: "Unknown error")
                }
            }
        }
        return req.second.status == HttpStatusCode.OK
    }

    /**
     * Checks if the user has a specific role in the given Discord guild.
     *
     * @param guildId The ID of the Discord guild.
     * @param roleId The ID of the Discord role to check for.
     * @return `true` if the user has the specified role in the guild, `false` otherwise.
     * @throws InvalidParameterException If the request parameters are invalid.
     * @throws PlatformIdNotLinkedException If the user's platform ID is not linked.
     * @throws GuildNotAuthorizedException If the application owner is not authorized for the guild.
     * @throws ApplicationNotFoundException If the application is not found.
     * @throws GuildPermissionsNotFoundException If the guild permissions are not found.
     */
    fun hasRole(guildId: Long, roleId: Long): Boolean {
        val req = request<JsonObject>(
            HttpMethod.Get,
            "${fromPlatform.name.lowercase()}/$fromUserId/discord/$guildId/role/$roleId"
        )

        val status = req.second.status
        val errMsg = req.first?.get("error")?.jsonObject?.get("message")?.toString()
        when (status) {
            HttpStatusCode.BadRequest ->
                throw InvalidParameterException(errMsg ?: "Invalid parameter")
            HttpStatusCode.Unauthorized ->
                throw PlatformIdNotLinkedException(errMsg ?: "Platform ID not linked")
            HttpStatusCode.Forbidden ->
                throw GuildNotAuthorizedException(errMsg ?: "Guild not authorized")
            HttpStatusCode.NotFound -> {
                when (req.first?.get("error")?.jsonObject["code"]?.toString()?.trim('"')) {
                    "guilds_not_found" -> throw GuildNotAuthorizedException(errMsg ?: "Guilds not found")
                    "application_not_found" -> throw ApplicationNotFoundException(errMsg ?: "Application not found")
                    "guild_permissions_not_found" -> throw GuildPermissionsNotFoundException(errMsg ?: "Guild permissions not found")
                    else -> throw InvalidParameterException(errMsg ?: "Invalid parameter")
                }
            }
        }
        return req.second.status == HttpStatusCode.OK
    }

}