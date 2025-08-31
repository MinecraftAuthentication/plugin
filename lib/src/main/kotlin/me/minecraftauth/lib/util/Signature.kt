package me.minecraftauth.lib.util

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.util.Base64

class Signature(privateKeyB64: String, data: String) {
    val key: String

    init {
        val privateKeyBytes = Base64.getUrlDecoder().decode(privateKeyB64)
        val keySpec = Ed25519PrivateKeyParameters(privateKeyBytes, 0)

        val signer = Ed25519Signer()
        signer.init(true, keySpec)
        signer.update(data.toByteArray(), 0, data.toByteArray().size)
        val signature = signer.generateSignature()

        key = Base64.getUrlEncoder().withoutPadding().encodeToString(signature)
    }
}
