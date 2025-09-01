package me.minecraftauth.game.config.function

import com.udojava.evalex.Expression
import me.minecraftauth.lib.MCAuth
import me.minecraftauth.lib.data.Platform
import java.util.Objects
import java.util.UUID

class YouTubeMemberTierFunction(private val uuid: UUID, private val api: MCAuth) : AbstractFunction("YouTubeMemberAt", 1) {

    override fun lazyEval(params: List<Expression.LazyNumber>): Expression.LazyNumber? {
        val tier = params[1].string
        Objects.requireNonNull(tier, "No tier ID given")

        return cache(javaClass.simpleName, uuid.toString(), null) {
            try {
                val resp = api.from(Platform.MINECRAFT, uuid.toString())
                    .youtube()
                    .isMember(tier)
                return@cache if (resp) TRUE else FALSE
            } catch (e: Exception) {
                e.printStackTrace()
                return@cache FALSE
            }
        }
    }

}