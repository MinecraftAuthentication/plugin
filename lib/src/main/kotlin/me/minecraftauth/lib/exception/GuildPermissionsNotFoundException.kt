package me.minecraftauth.lib.exception

import java.lang.RuntimeException

class GuildPermissionsNotFoundException(msg: String) : RuntimeException(msg) {}