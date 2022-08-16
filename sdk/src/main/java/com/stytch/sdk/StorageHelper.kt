package com.stytch.sdk

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

private const val KEY_ALIAS = "Stytch KeyStore Alias"
private const val PREFERENCES_FILE_NAME = "stytch_preferences"

internal class StorageHelper(context: Context) {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    init {
        keyStore.load(null)
        createNewKeys(context, keyStore, KEY_ALIAS)
    }

    internal fun saveValue(name: String, value: String) {
        val encryptedData = encryptString(value)
        with(sharedPreferences.edit()) {
            putString(name, encryptedData)
            apply()
        }
    }

    internal fun loadValue(name: String): String? {
        val encryptedString = sharedPreferences.getString(name, null)
        return decryptString(encryptedString)
    }

    private fun encryptString(plainText: String): String? {
        var encodedString: String? = null
        try {
            val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
            val publicKey = privateKeyEntry.certificate.publicKey

            // Encrypt the text
            val input: Cipher = getCipher()
            input.init(Cipher.ENCRYPT_MODE, publicKey)
            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(
                outputStream, input)
            cipherOutputStream.write(plainText.toByteArray(charset("UTF-8")))
            cipherOutputStream.close()
            val vals: ByteArray = outputStream.toByteArray()
            encodedString = Base64.encodeToString(vals, Base64.DEFAULT)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return encodedString
    }

    private fun decryptString(encryptedText: String?): String? {
        var decryptedText: String? = null
        try {
            val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
            val privateKey = privateKeyEntry.privateKey
            val output = getCipher()
            output.init(Cipher.DECRYPT_MODE, privateKey)
            val cipherInputStream = CipherInputStream(
                ByteArrayInputStream(Base64.decode(encryptedText, Base64.DEFAULT)), output)
            val values: ArrayList<Byte> = ArrayList()
            var nextByte: Int
            while (cipherInputStream.read().also { nextByte = it } != -1) {
                values.add(nextByte.toByte())
            }
            val bytes = ByteArray(values.size)
            for (i in bytes.indices) {
                bytes[i] = values[i]
            }
            decryptedText = String(bytes, 0, bytes.size, Charset.forName("UTF-8"))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return decryptedText
    }

    private fun createNewKeys(context: Context, keyStore: KeyStore, alias: String) {
        try {
            // Create new key if needed
            if (!keyStore.containsAlias(alias)) {
                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                end.add(Calendar.YEAR, 1)
                val spec = KeyPairGeneratorSpec.Builder(context)
                    .setAlias(alias)
                    .setSubject(X500Principal("CN=Sample Name, O=Android Authority"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()
                val generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")
                generator.initialize(spec)
                generator.generateKeyPair()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getCipher(): Cipher {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
            return Cipher.getInstance("RSA/ECB/PKCS1Padding",
                "AndroidOpenSSL"); // error in android 6: InvalidKeyException: Need RSA private or public key
        } else { // android m and above
            return Cipher.getInstance("RSA/ECB/PKCS1Padding",
                "AndroidKeyStoreBCWorkaround"); // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
        }
    }

}