package me.minecraftauth.lib.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import java.io.IOException
import kotlin.reflect.full.createType

private val cioEngine = CIO.create {
    // no logging configuration, purely silent
}

val http = HttpClient(cioEngine) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            explicitNulls = true
        })
    }
}

@Throws(IOException::class)
inline fun <reified T> request(
    method: HttpMethod,
    url: String,
    contentType: ContentType = ContentType.Application.Json,
    headers: Map<String, String> = emptyMap(),
    bearer: String? = null,
    body: Any? = null,
    debug: Boolean = false
): Pair<T?, HttpResponse> = runBlocking {
    require(T::class != Any::class)
    try {
        val response = http.request(url) {
            this.method = method
            contentType(contentType)
            bearer?.let { bearerAuth(bearer) }

            headers {
                headers.forEach { (k, v) -> append(k, v) }
            }

            when (body) {
                null -> {}
                is ByteArray,
                is String,
                is FormDataContent,
                is MultiPartFormDataContent -> setBody(body)
                else -> {
                    try {
                        val serializer = serializer(body::class.createType())
                        val json = Json.encodeToString(serializer, body)
                        setBody(json)
                    } catch (t: Throwable) {
                        throw IOException("Failed to serialize request body of type ${body::class.qualifiedName}", t)
                    }
                }
            }

            if (debug) {
                println("-> $method $url ($headers)")
                if (body != null) println("[${body::class.qualifiedName}] $body")
            }
        }

        val body = response.bodyAsText()
        if (debug || !response.status.isSuccess()) {
            println("<- ${response.status} @ $method $url [len=${body.length}]")
            if (body.isNotBlank()) println(body)
        }

        if (body.isNotBlank()) {
            val parsed = when (T::class) {
                Unit::class -> null
                String::class -> body
                JsonArray::class -> if (body.startsWith("[")) Json.decodeFromString<JsonArray>(body) else null
                JsonElement::class, JsonObject::class -> if (body.startsWith("{")) parseJsonObject(body) else null
                else -> Json.decodeFromString(body)
            } as T
            return@runBlocking parsed to response
        } else {
            return@runBlocking null to response
        }
    } catch (t: Throwable) {
        throw IOException("Error during HTTP request: ${t.message}", t)
    }
}

fun parseJsonObject(text: String?, lenient: Boolean = true): JsonObject {
    if (text.isNullOrBlank()) return buildJsonObject {}
    return try {
        Json.parseToJsonElement(text) as JsonObject
    } catch (t: Throwable) {
        if (lenient) {
            buildJsonObject {
                put("success", false)
                put("error", t.message ?: "unknown error")
            }
        } else {
            throw t
        }
    }
}
