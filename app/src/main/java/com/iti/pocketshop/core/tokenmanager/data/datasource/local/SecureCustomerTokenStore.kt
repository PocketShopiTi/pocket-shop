package com.iti.pocketshop.core.tokenmanager.data.datasource.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import com.iti.pocketshop.core.tokenmanager.domain.model.CustomerSession
import dagger.hilt.android.qualifiers.ApplicationContext
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
    }

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun read(): CustomerSession? {
        val encrypted = preferences.getString(KEY_PAYLOAD, null) ?: return null
        val iv = preferences.getString(KEY_IV, null) ?: return null
        return runCatching {
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(
                    Cipher.DECRYPT_MODE,
                    getOrCreateKey(),
                    GCMParameterSpec(TAG_LENGTH_BITS, Base64.decode(iv, Base64.NO_WRAP)),
                )
            }
            val parts = cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP))
                .toString(Charsets.UTF_8)
                .split('\n', limit = 3)
            require(parts.size == 3)
            CustomerSession(
                ownerUid = parts[0],
                accessToken = parts[1],
                expiresAt = Instant.parse(parts[2]),
            )
        }.getOrElse {
            clear()
            null
        }
    }

    override fun save(session: CustomerSession) {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }
        val payload = "${session.ownerUid}\n${session.accessToken}\n${session.expiresAt}"
            .toByteArray(Charsets.UTF_8)
        preferences.edit {
            putString(KEY_PAYLOAD, Base64.encodeToString(cipher.doFinal(payload), Base64.NO_WRAP))
                .putString(KEY_IV, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
        }
    }

    override fun clear() {
        preferences.edit { clear() }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        return (keyStore.getKey(KEY_ALIAS, null) as? SecretKey) ?: KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                )
            }
            .generateKey()
    }


}
