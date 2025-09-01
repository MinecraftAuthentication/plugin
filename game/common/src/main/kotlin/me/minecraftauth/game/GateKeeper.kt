package me.minecraftauth.game

import com.udojava.evalex.AbstractOperator
import com.udojava.evalex.Operator
import github.scarsz.configuralize.DynamicConfig
import me.minecraftauth.game.config.Server
import me.minecraftauth.lib.MCAuth
import java.math.BigDecimal
import java.util.Objects

class GateKeeper(private val config: DynamicConfig, private val api: MCAuth) {

    private val servers: MutableMap<String, Server> = mutableMapOf()
    private val operators: MutableSet<Operator> = mutableSetOf()

    fun getOperators(): Set<Operator> {
        return this.operators
    }

    init {
        this.operators.add(object : AbstractOperator("and", 4, false, true) {
            override fun eval(v1: BigDecimal, v2: BigDecimal): BigDecimal {
                Objects.requireNonNull(v1, "No left boolean for AND operator")
                Objects.requireNonNull(v2, "No right boolean for AND operator")

                val b1 = v1.compareTo(BigDecimal.ZERO) != 0

                if (!b1) return BigDecimal.ZERO
                else {
                    val b2 = v2.compareTo(BigDecimal.ZERO) != 0
                    return if (b2) BigDecimal.ONE else BigDecimal.ZERO
                }
            }
        })

        reload()
    }

    fun reload() {
        servers.clear()

        val superServer = Server(config.dget("super"), "super", api, this)
        if (superServer.getExpressions().isNotEmpty()) servers.put("super", superServer)

        val proxyServers = config.dgetSilent("proxy.servers")
        if (proxyServers.isPresent) {
            proxyServers.children().forEach {
                val server = it.key().convert().intoString()
                servers.put(server, Server(it, server, api, this))
            }
        }

        val onlySuper = servers.keys.stream().allMatch(Objects::isNull)
        val expressionCount = servers.values.stream().mapToInt { it.getExpressions().size }.sum()

        println(StringBuilder()
            .append("[MCAuth] Controlling entry ")
            .append(if (!onlySuper) "to ${servers.size} server${if(servers.size > 1) "s" else ""}" else "")
            .append("based on $expressionCount conditions")
        )
    }

    fun getServer(name: String): Server? {
        return servers[name]
    }

}