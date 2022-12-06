package com.stytch.sdk

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val BIOMETRICS_ENROLLMENT_ID = 555
internal const val AUTHENTICATION_FAILED = "Authentication Failed"

public class BiometricsProviderImpl : BiometricsProvider {
    override suspend fun showBiometricPrompt(
        context: FragmentActivity,
        promptInfo: BiometricPrompt.PromptInfo?
    ): Unit = suspendCoroutine { continuation ->
        val executor = Executors.newSingleThreadExecutor()
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                continuation.resumeWithException(StytchExceptions.Input(errString.toString()))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                continuation.resume(Unit)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                continuation.resumeWithException(StytchExceptions.Input(AUTHENTICATION_FAILED))
            }
        }
        val prompt = promptInfo ?: BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()
        BiometricPrompt(context, executor, callback).authenticate(prompt)
    }

    override fun areBiometricsAvailable(context: FragmentActivity): Pair<Boolean, String> {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> Pair(true, "Biometrics are ready to be used.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Pair(false, "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Pair(false, "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // Prompts the user to create credentials that your app accepts.
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BiometricManager.Authenticators.BIOMETRIC_STRONG
                        )
                    }
                    context.startActivityForResult(enrollIntent, BIOMETRICS_ENROLLMENT_ID)
                }
                Pair(false, "No biometrics currently enrolled on device. Starting biometrics enrollment flow.")
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                Pair(false, "A security vulnerability has been discovered with one or more hardware sensors.")
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                Pair(false, "The requested biometrics options are incompatible with the current Android version.")
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                // technically, we could still _try_ to authenticate, but there's no guarantee it would work
                Pair(false, "Unable to determine whether the user can authenticate.")
            else -> Pair(false, "Unknown")
        }
    }
}
