package com.stytch.sessions

import com.stytch.sdk.AuthResponse
import com.stytch.sdk.BaseResponse
import com.stytch.sdk.LoginOrCreateUserByEmailResponse
import com.stytch.sdk.Sessions
import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchExceptions
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SessionsImpl internal constructor() : Sessions {
    override val sessionToken: String?
        get() {
            try {
                return StytchClient.sessionStorage.sessionToken
            } catch (ex: Exception) {
                throw StytchExceptions.Critical(ex)
            }
        }

    override val sessionJwt: String?
        get() {
            try {
                return StytchClient.sessionStorage.sessionJwt
            } catch (ex: Exception) {
                throw StytchExceptions.Critical(ex)
            }
        }

    override suspend fun authenticate(authParams: Sessions.AuthParams): AuthResponse {
        val result: AuthResponse
        withContext(StytchClient.ioDispatcher) {

            // do not revoke session here since we using stored data to authenticate

            // call backend endpoint
            result = StytchApi.Sessions.authenticate(
                authParams.sessionDurationMinutes?.toInt()
            ).apply {
                launchSessionUpdater()
            }
        }
        return result

    }

    override fun authenticate(authParams: Sessions.AuthParams, callback: (AuthResponse) -> Unit) {
//          call endpoint in IO thread
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(authParams)
//          change to main thread to call callback
            callback(result)
        }
    }

    override suspend fun revoke(): BaseResponse {
        var result: LoginOrCreateUserByEmailResponse
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.Sessions.revoke()
        }
//            remove stored session
        try {
            StytchClient.sessionStorage.revoke()
        } catch (ex: Exception) {
            result = StytchResult.Error(StytchExceptions.Critical(ex))
        }
        return result
    }

    override fun revoke(callback: (BaseResponse) -> Unit) {
//          call endpoint in IO thread
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = revoke()
//          change to main thread to call callback
            callback(result)
        }
    }

    /**
     * @throws StytchExceptions.Critical if failed to save data
     */
    override fun updateSession(sessionToken: String?, sessionJwt: String?) {
        try {
            StytchClient.sessionStorage.updateSession(sessionToken, sessionJwt)
        } catch (ex: Exception) {
            throw StytchExceptions.Critical(ex)
        }
    }
}
