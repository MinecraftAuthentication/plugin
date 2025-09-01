package me.minecraftauth.game.config.function

import com.udojava.evalex.Expression
import me.minecraftauth.lib.MCAuth
import me.minecraftauth.lib.enum.Platform
import java.util.Objects
import java.util.UUID

class TwitchSubscriberTierFunction(private val uuid: UUID, private val api: MCAuth) : AbstractFunction("TwitchSubscriberAt", 1) {

    override fun lazyEval(params: List<Expression.LazyNumber>): Expression.LazyNumber? {
        val tier = params[1].string
        Objects.requireNonNull(tier, "No tier ID given")

        return cache(javaClass.simpleName, uuid.toString(), null) {
            try {
                val resp = api.from(Platform.MINECRAFT, uuid.toString())
                    .twitch()
                    .isSubscribed(tier)
                return@cache if (resp) TRUE else FALSE
            } catch (e: Exception) {
                e.printStackTrace()
                return@cache FALSE
            }
        }
    }

}