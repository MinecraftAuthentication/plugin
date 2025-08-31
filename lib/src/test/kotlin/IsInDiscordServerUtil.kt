import me.minecraftauth.lib.AuthConfig
import me.minecraftauth.lib.MCAuth
import me.minecraftauth.lib.enum.Platform

fun main() {
    println("Hello, MinecraftAuth!")

    val resp = MCAuth(AuthConfig(
        1394111271304953856,
        "Ol9edT-PN56cwJr57GvIP8Dle4gjg5yULieuQzd6sUA=",
        debug = true
    )).from(Platform.DISCORD, "95088531931672576").discord().isInGuild(1065406608605192312)
    println(resp)
}
