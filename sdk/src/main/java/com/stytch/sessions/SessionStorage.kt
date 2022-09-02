package com.stytch.sessions

import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sdk.network.responseData.SessionData
import com.stytch.sdk.network.responseData.UserData

private const val PREFERENCES_NAME_SESSION_JWT = "session_jwt"
private const val PREFERENCES_NAME_SESSION_TOKEN = "session_token"

internal class SessionStorage {
    var sessionToken: String?
        private set(value) {
            StytchClient.storageHelper.saveValue(PREFERENCES_NAME_SESSION_TOKEN, value)
        }
        get() {
            return StytchClient.storageHelper.loadValue(PREFERENCES_NAME_SESSION_TOKEN)
        }

    var sessionJwt: String?
        private set(value) {
            StytchClient.storageHelper.saveValue(PREFERENCES_NAME_SESSION_JWT, value)
        }
        get() {
            return StytchClient.storageHelper.loadValue(PREFERENCES_NAME_SESSION_JWT)
        }

    var session: SessionData? = null
        private set(value) {
            field = value
        }

    var user: UserData? = null

    fun updateSession(sessionToken: String?, sessionJwt: String?, session: SessionData? = null) {
        this.sessionToken = sessionToken
        this.sessionJwt = sessionJwt
        this.session = session
    }

    fun revoke() {
        sessionToken = null
        sessionJwt = null
        session = null
        user = null
    }

}

//    save session data
internal fun StytchResult<AuthData>.saveSession(): StytchResult<AuthData> {
    if (this is StytchResult.Success){
        value.apply {
//            save session tokens
            StytchClient.sessionStorage.updateSession(session_token, session_jwt, session)
//            save user
            StytchClient.sessionStorage.user = user
        }
    }
    return this
}