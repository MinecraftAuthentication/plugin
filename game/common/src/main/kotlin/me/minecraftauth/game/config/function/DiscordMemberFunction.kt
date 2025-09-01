package me.minecraftauth.game.config.function

import com.udojava.evalex.Expression
import me.minecraftauth.lib.MCAuth
import me.minecraftauth.lib.enum.Platform
import java.util.Objects
import java.util.UUID

class DiscordMemberFunction(private val uuid: UUID, private val api: MCAuth) : AbstractFunction("DiscordMember", 1) {

    override fun lazyEval(params: List<Expression.LazyNumber>): Expression.LazyNumber? {
        val guild = params[0].string
        Objects.requireNonNull(guild, "No guild ID given")

        return cache(javaClass.simpleName, uuid.toString(), guild) {
            try {
                val resp = api.from(Platform.MINECRAFT, uuid.toString())
                    .discord()
                    .isInGuild(guild.toLong())
                return@cache if (resp) TRUE else FALSE
            } catch (e: Exception) {
                e.printStackTrace()
                return@cache FALSE
            }
        }
    }

}