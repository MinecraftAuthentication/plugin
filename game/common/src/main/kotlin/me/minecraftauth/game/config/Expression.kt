package me.minecraftauth.game.config

import com.udojava.evalex.Expression

class Expression(expr: String) : Expression(expr) {

    var success: Int = 0

    fun incrementSuccessCount(): Int {
        return ++success
    }

}