package me.minecraftauth.game.config

class GatekeeperResult {
    val type: Type?

    val message: String?

    constructor(type: Type) {
        this.type = type
        this.message = ""
    }

    constructor(type: Type, message: String) {
        this.type = type
        this.message = message
    }

    enum class Type {
        NOT_ENABLED,
        DENIED(true),
        ALLOWED,
        BYPASSED;

        private val willDenyLogin: Boolean

        constructor() {
            this.willDenyLogin = false
        }

        constructor(willDenyLogin: Boolean) {
            this.willDenyLogin = willDenyLogin
        }

        fun willDenyLogin(): Boolean {
            return willDenyLogin
        }
    }
}