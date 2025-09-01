package me.minecraftauth.game.config.function

import com.udojava.evalex.Expression
import me.minecraftauth.lib.MCAuth
import me.minecraftauth.lib.data.Platform
import java.util.UUID

class PatreonMemberFunction(private val uuid: UUID, private val api: MCAuth) : AbstractFunction("PatreonMember", 0) {

    override fun lazyEval(params: List<Expression.LazyNumber>): Expression.LazyNumber? {

        return cache(javaClass.simpleName, uuid.toString(), null) {
            try {
                val resp = api.from(Platform.MINECRAFT, uuid.toString())
                    .patreon()
                    .isMember()
                return@cache if (resp) TRUE else FALSE
            } catch (e: Exception) {
                e.printStackTrace()
                return@cache FALSE
            }
        }
    }

}