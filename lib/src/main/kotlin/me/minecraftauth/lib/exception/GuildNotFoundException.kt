package me.minecraftauth.lib.exception

import java.lang.RuntimeException

class GuildNotFoundException(msg: String) : RuntimeException(msg) {}