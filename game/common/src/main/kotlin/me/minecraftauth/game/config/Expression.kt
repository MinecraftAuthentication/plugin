package me.minecraftauth.game.config

class Expression(expr: String) : com.udojava.evalex.Expression(expr) {

    var success: Int = 0

    fun incrementSuccessCount(): Int {
        return ++success
    }

}