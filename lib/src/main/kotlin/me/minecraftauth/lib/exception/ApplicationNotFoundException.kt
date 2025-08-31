package me.minecraftauth.lib.exception

import java.lang.RuntimeException

class ApplicationNotFoundException(msg: String) : RuntimeException(msg) {}