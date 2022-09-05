package com.stytch.sessions

import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sdk.network.responseData.SessionData

private const val PREFERENCES_NAME_SESSION_JWT = "session_jwt"
private const val PREFERENCES_NAME_SESSION_TOKEN = "session_token"

internal class SessionStorage {
    var sessionToken: String?
        private set(value) {
            StytchClient.storageHelper.saveValue(PREFERENCES_NAME_SESSION_TOKEN, value)
        }
        get() {
            val value: String?
            synchronized(this) {
                value = StytchClient.storageHelper.loadValue(PREFERENCES_NAME_SESSION_TOKEN)
            }
            return value
        }

    var sessionJwt: String?
        private set(value) {
            StytchClient.storageHelper.saveValue(PREFERENCES_NAME_SESSION_JWT, value)
        }
        get() {
            val value: String?
            synchronized(this) {
                value = StytchClient.storageHelper.loadValue(PREFERENCES_NAME_SESSION_JWT)
            }
            return value ?: ""
        }

    var session: SessionData? = null
        private set(value) {
            field = value
        }

    fun updateSession(sessionToken: String?, sessionJwt: String?, session: SessionData? = null) {
        synchronized(this) {
            this.sessionToken = sessionToken
            this.sessionJwt = sessionJwt
            this.session = session
        }
    }

    fun revoke() {
        synchronized(this) {
            sessionToken = null
            sessionJwt = null
            session = null
        }
    }

}

//    save session data
internal fun StytchResult<AuthData>.saveSession(): StytchResult<AuthData> {
    if (this is StytchResult.Success) {
        value.apply {
            StytchClient.sessionStorage.updateSession(sessionToken, sessionJwt, session)
        }
    }
    return this
}