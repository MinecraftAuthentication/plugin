package me.minecraftauth.lib.platform

import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import me.minecraftauth.lib.AuthConfig
import me.minecraftauth.lib.data.Platform
import me.minecraftauth.lib.util.Signature
import java.util.Base64
import java.util.UUID

open class BasePlatformClient(
    protected val fromPlatform: Platform,
    protected val fromUserId: String,
    protected val config: AuthConfig
) {

    protected inline fun <reified T> request(
        method: HttpMethod,
        url: String,
        contentType: ContentType = ContentType.Application.Json,
        headers: Map<String, String> = emptyMap(),
        bearer: String? = null,
        body: Any? = null
    ): Pair<T?, HttpResponse> {
        val nonce = Base64.getEncoder().encodeToString(
            (UUID.randomUUID().toString() + System.nanoTime() + (0..1000).random()
        ).toByteArray())
        val sig = getSignature(method, url, nonce)
        val appId = config.appId.toString()
        if (config.debug) {
            println("[$fromPlatform:$fromUserId] Making request: $method $url")
            println("[$fromPlatform:$fromUserId] X-Signature: $sig")
            println("[$fromPlatform:$fromUserId] X-Application-ID: $appId")
            println("[$fromPlatform:$fromUserId] X-Nonce: $nonce")
        } else {
            println("[$fromPlatform:$fromUserId] Making request: $method $url")
        }
        return me.minecraftauth.lib.util.request(
            method,
            "${config.apiHost}/${config.apiVersion}/$url",
            contentType,
            headers + mapOf(
                "X-Signature" to getSignature(method, url, nonce),
                "X-Application-ID" to config.appId.toString(),
                "X-Nonce" to nonce
            ),
            bearer,
            body,
            debug = config.debug
        )
    }

    protected fun getSignature(method: HttpMethod, url: String, nonce: String): String {
        val data = "${method.value} $url $nonce"
        return Signature(config.token, data).key
    }

}