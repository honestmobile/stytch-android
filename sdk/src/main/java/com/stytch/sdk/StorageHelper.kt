package com.stytch.sdk

import android.content.Context
import android.content.SharedPreferences
import java.security.KeyStore

private const val KEY_ALIAS = "Stytch RSA 2048"
private const val ED25519_KEY_ALIAS = "Stytch Ed25519"
private const val PREFERENCES_FILE_NAME = "stytch_preferences"
internal const val PREFERENCES_CODE_VERIFIER = "code_verifier"

internal object StorageHelper {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        keyStore.load(null)
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        EncryptionManager.createNewKeys(context, KEY_ALIAS, ED25519_KEY_ALIAS)
    }

    /**
     * Encrypt and save value to SharedPreferences
     * @throws Exception if failed to encrypt string
     */
    internal fun saveValue(name: String, value: String?) {
        if (value == null) {
            with(sharedPreferences.edit()) {
                putString(name, value)
                apply()
            }
            return
        }

        val encryptedData = EncryptionManager.encryptString(value)
        with(sharedPreferences.edit()) {
            putString(name, encryptedData)
            apply()
        }
    }

    /**
     * Load and decrypt value from SharedPreferences
     * @return null if failed to load data
     */
    internal fun loadValue(name: String): String? {
        return try {
            val encryptedString = sharedPreferences.getString(name, null)
            EncryptionManager.decryptString(encryptedString)
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * @return Pair(codeChallengeMethod, codeChallenge)
     * @throws Exception if failed to encrypt data
     */
    internal fun generateHashedCodeChallenge(): Pair<String, String> {
        val codeVerifier: String?

        codeVerifier = EncryptionManager.generateCodeChallenge()
        saveValue(PREFERENCES_CODE_VERIFIER, codeVerifier)

        return "S256" to EncryptionManager.encryptCodeChallenge(codeVerifier)
    }

    /**
     * @return publicKey?
     */
    internal fun getEd25519PublicKey(): String? = try {
        EncryptionManager.getOrGenerateEd25519PublicKey()
    } catch (ex: Exception) {
        StytchLog.e(ex.message ?: "Failed to get ED25519 public key")
        null
    }

    internal fun signEd25519CodeChallenge(challenge: String): String? = try {
        EncryptionManager.signEd25519CodeChallenge(challenge)
    } catch (ex: Exception) {
        StytchLog.e(ex.message ?: "Failed to sign challenge")
        null
    }

    internal fun deleteEd25519Key(): Unit = EncryptionManager.deleteEd25519Key()
}
