package me.minecraftauth.lib.exception

import java.lang.RuntimeException

class NoTierFoundException(msg: String) : RuntimeException(msg) {}