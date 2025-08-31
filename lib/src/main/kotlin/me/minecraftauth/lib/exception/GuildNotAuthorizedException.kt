package me.minecraftauth.lib.exception

import java.lang.RuntimeException

class GuildNotAuthorizedException(msg: String) : RuntimeException(msg) {}