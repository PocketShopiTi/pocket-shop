package com.iti.pocketshop.core.sessionmanager.data.datasource.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import com.iti.pocketshop.core.sessionmanager.domain.model.CustomerSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.security.KeyStore
import java.time.Instant
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class SecureCustomerTokenStore @Inject constructor(
    @ApplicationContext context: Context,
) : CustomerTokenStore {

    private companion object {
        const val PREFERENCES_NAME = "shopify_session"
        const val KEY_PAYLOAD = "payload"
        const val KEY_IV = "iv"

        const val KEY_ALIAS = "pocket_shop_shopify_session"
        const val KEYSTORE_PROVIDER = "AndroidKeyStore"

        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val TAG_LENGTH_BITS = 128
        const val KEY_SIZE_BITS = 256
    }

    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override suspend fun read(): CustomerSession? = withContext(Dispatchers.IO) {
        val encryptedPayload = preferences.getString(KEY_PAYLOAD, null)
        val iv = preferences.getString(KEY_IV, null)

        if (encryptedPayload == null && iv == null) return@withContext null
        if (encryptedPayload == null || iv == null) {
            clear()
            return@withContext null
        }

        return@withContext runCatching {
            val key = getExistingKey() ?: error("Missing encryption key")

            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(
                    Cipher.DECRYPT_MODE,
                    key,
                    GCMParameterSpec(
                        TAG_LENGTH_BITS,
                        Base64.decode(iv, Base64.NO_WRAP),
                    ),
                )
            }

            val json = cipher.doFinal(
                Base64.decode(encryptedPayload, Base64.NO_WRAP),
            ).toString(Charsets.UTF_8)

            json.toCustomerSession()
        }.getOrElse {
            clear()
            null
        }
    }

    override suspend fun save(session: CustomerSession) = withContext(Dispatchers.IO) {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }

        val encryptedPayload = cipher.doFinal(
            session.toJson().toByteArray(Charsets.UTF_8),
        )

        preferences.edit(commit = true) {
            putString(
                KEY_PAYLOAD,
                Base64.encodeToString(encryptedPayload, Base64.NO_WRAP),
            )
            putString(
                KEY_IV,
                Base64.encodeToString(cipher.iv, Base64.NO_WRAP),
            )
        }
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        preferences.edit(commit = true) {
            remove(KEY_PAYLOAD)
            remove(KEY_IV)
        }
    }

    private fun getExistingKey(): SecretKey? {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey
    }

    private fun getOrCreateKey(): SecretKey {
        getExistingKey()?.let { return it }

        return KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                    )
                        .setKeySize(KEY_SIZE_BITS)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(true)
                        .build(),
                )
            }
            .generateKey()
    }

    private fun CustomerSession.toJson(): String {
        return JSONObject()
            .put("ownerUid", ownerUid)
            .put("accessToken", accessToken)
            .put("expiresAt", expiresAt.toString())
            .toString()
    }

    private fun String.toCustomerSession(): CustomerSession {
        val json = JSONObject(this)

        return CustomerSession(
            ownerUid = json.getString("ownerUid"),
            accessToken = json.getString("accessToken"),
            expiresAt = Instant.parse(json.getString("expiresAt")),
        )
    }
}
