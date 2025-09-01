package me.minecraftauth.game.config

import alexh.weak.Dynamic
import me.minecraftauth.game.GateKeeper
import me.minecraftauth.game.config.function.AbstractFunction
import me.minecraftauth.game.config.function.DiscordMemberFunction
import me.minecraftauth.game.config.function.DiscordRoleFunction
import me.minecraftauth.game.config.function.PatreonMemberFunction
import me.minecraftauth.game.config.function.PatreonMemberTierFunction
import me.minecraftauth.game.config.function.TwitchFollowerFunction
import me.minecraftauth.game.config.function.TwitchSubscriberFunction
import me.minecraftauth.game.config.function.TwitchSubscriberTierFunction
import me.minecraftauth.game.config.function.YouTubeMemberFunction
import me.minecraftauth.game.config.function.YouTubeMemberTierFunction
import me.minecraftauth.game.config.function.YouTubeSubscriberFunction
import me.minecraftauth.lib.MCAuth
import java.math.BigDecimal
import java.util.Comparator
import java.util.UUID

class Server(private val config: Dynamic, private val server: String, private val api: MCAuth, private val keeper: GateKeeper) {

    private val kick: String = config.get("kick_message").asString()

    fun getExpressions(): List<Expression> {
        return getExpressions(UUID.randomUUID())
    }

    private fun getFunctions(uuid: UUID): List<AbstractFunction> {
        val list = mutableListOf<AbstractFunction>()

        list.addAll(listOf(
            DiscordMemberFunction(uuid, api),
            DiscordRoleFunction(uuid, api),
            PatreonMemberFunction(uuid, api),
            PatreonMemberTierFunction(uuid, api),
            TwitchFollowerFunction(uuid, api),
            TwitchSubscriberFunction(uuid, api),
            TwitchSubscriberTierFunction(uuid, api),
            YouTubeMemberFunction(uuid, api),
            YouTubeMemberTierFunction(uuid, api),
            YouTubeSubscriberFunction(uuid, api)
        ))

        return list
    }

    private fun getExpressions(uuid: UUID): List<Expression> {
        val list = mutableListOf<Expression>()
        config.get("conditions").children().forEach { it ->
            val expr = Expression(it.asString())
            getFunctions(uuid).forEach { func -> expr.addLazyFunction(func) }
            keeper.getOperators().forEach { operator -> expr.addOperator(operator) }
            list.add(expr)
        }
        return list
    }

    fun verify(uuid: UUID): GatekeeperResult {
        var first = true
        val exprs = getExpressions(uuid)
        exprs.forEach { it ->
            if (it.eval().compareTo(BigDecimal.ONE) == 0) {
                it.incrementSuccessCount()
                if (!first) exprs.sortedBy { expr -> -expr.success }
                return GatekeeperResult(GatekeeperResult.Type.ALLOWED)
            }
            first = false
        }
        return GatekeeperResult(GatekeeperResult.Type.DENIED, kick)
    }

}