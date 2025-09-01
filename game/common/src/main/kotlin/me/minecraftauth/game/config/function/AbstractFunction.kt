package me.minecraftauth.game.config.function

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.udojava.evalex.AbstractLazyFunction
import com.udojava.evalex.Expression
import com.udojava.evalex.Expression.LazyNumber
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


abstract class AbstractFunction(name: String, numParams: Int) : AbstractLazyFunction(name, numParams) {

    companion object {
        private val VALUE_CACHE: Cache<String, LazyNumber> = Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).build()

        val TRUE: LazyNumber = object : LazyNumber {
            override fun eval(): BigDecimal {
                return BigDecimal.ONE
            }

            override fun getString(): String {
                return "1"
            }
        }

        val FALSE: LazyNumber = object : LazyNumber {
            override fun eval(): BigDecimal {
                return BigDecimal.ZERO
            }

            override fun getString(): String {
                return "0"
            }
        }

    }

    fun cache(func: String, acc: String, data: String?, compute: Supplier<LazyNumber>): LazyNumber {
        return VALUE_CACHE.get(
            func + "." + acc + (if (data != null) ".$data" else ""),
            { s -> compute.get() })!!
    }




}