package me.minecraftauth.game.config.function

import com.udojava.evalex.Expression
import me.minecraftauth.lib.MCAuth
import me.minecraftauth.lib.enum.Platform
import java.util.Objects
import java.util.UUID

class DiscordRoleFunction(private val uuid: UUID, private val api: MCAuth) : AbstractFunction("DiscordRole", 2) {

    override fun lazyEval(params: List<Expression.LazyNumber>): Expression.LazyNumber? {
        val guild = params[0].string
        val role = params[1].string
        Objects.requireNonNull(guild, "No guild ID given")
        Objects.requireNonNull(role, "No role ID given")

        return cache(javaClass.simpleName, uuid.toString(), role) {
            try {
                val resp = api.from(Platform.MINECRAFT, uuid.toString())
                    .discord()
                    .hasRole(guild.toLong(), role.toLong())
                return@cache if (resp) TRUE else FALSE
            } catch (e: Exception) {
                e.printStackTrace()
                return@cache FALSE
            }
        }
    }

}