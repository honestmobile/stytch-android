package com.stytch.sdk.consumer.oauth

import android.content.IntentSender
import androidx.annotation.VisibleForTesting
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchLog
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.StytchErrorType
import com.stytch.sdk.common.sso.GoogleOneTapProvider
import com.stytch.sdk.consumer.NativeOAuthResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

internal class GoogleOneTapImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.OAuth,
    private val googleOneTapProvider: GoogleOneTapProvider,
) : OAuth.GoogleOneTap {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal lateinit var nonce: String

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal lateinit var oneTapClient: SignInClient

    override suspend fun start(parameters: OAuth.GoogleOneTap.StartParameters): Boolean {
        return try {
            nonce = EncryptionManager.encryptCodeChallenge(EncryptionManager.generateCodeChallenge())
            oneTapClient = googleOneTapProvider.getSignInClient(context = parameters.context)
            val signInRequest = googleOneTapProvider.getSignInRequest(
                clientId = parameters.clientId,
                nonce = nonce,
                autoSelectEnabled = parameters.autoSelectEnabled
            )
            suspendCancellableCoroutine { continuation ->
                oneTapClient
                    .beginSignIn(signInRequest)
                    .addOnSuccessListener(parameters.context) { result ->
                        try {
                            parameters.context.startIntentSenderForResult(
                                result.pendingIntent.intentSender, // the intent sender generated by the OneTap client
                                parameters.oAuthRequestIdentifier, // the request ID for retrieving the result
                                null, // If non-null, this will be provided as the intent parameter
                                0, // Intent flags in the original IntentSender that you would like to change
                                0, // Desired values for any bits set in flagsMask
                                0, // Always set to 0
                                null // Additional options for how the Activity should be started
                            )
                            continuation.resume(Unit)
                        } catch (e: IntentSender.SendIntentException) {
                            continuation.resumeWithException(e)
                        }
                    }
                    .addOnFailureListener(parameters.context) { e ->
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        continuation.resumeWithException(e)
                    }
            }
            true
        } catch (e: Exception) {
            StytchLog.e(e.message ?: "Error beginning Google Sign in flow")
            false
        }
    }

    override fun start(parameters: OAuth.GoogleOneTap.StartParameters, callback: (Boolean) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = start(parameters)
            callback(result)
        }
    }

    override suspend fun authenticate(parameters: OAuth.GoogleOneTap.AuthenticateParameters): NativeOAuthResponse {
        if (!::nonce.isInitialized || !::oneTapClient.isInitialized) {
            return StytchResult.Error(StytchExceptions.Input(StytchErrorType.GOOGLE_ONETAP_MISSING_MEMBER.message))
        }
        return withContext(dispatchers.io) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(parameters.data)
                val idToken = credential.googleIdToken
                    ?: return@withContext StytchResult.Error(
                        StytchExceptions.Input(StytchErrorType.GOOGLE_ONETAP_MISSING_ID_TOKEN.message)
                    )
                api.authenticateWithGoogleIdToken(
                    idToken = idToken,
                    nonce = nonce,
                    sessionDurationMinutes = parameters.sessionDurationMinutes
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            } catch (e: ApiException) {
                StytchResult.Error(StytchExceptions.Critical(e))
            }
        }
    }

    override fun authenticate(
        parameters: OAuth.GoogleOneTap.AuthenticateParameters,
        callback: (NativeOAuthResponse) -> Unit
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    override fun signOut() {
        if (::oneTapClient.isInitialized) {
            oneTapClient.signOut()
        }
    }
}
