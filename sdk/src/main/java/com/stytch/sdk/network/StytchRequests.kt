package com.stytch.sdk.network

import com.squareup.moshi.JsonClass

internal object StytchRequests {

    object MagicLinks {
        object Email {
            @JsonClass(generateAdapter = true)
            data class LoginOrCreateUserRequest(
                val email: String,
                val login_magic_link_url: String?,
                val code_challenge: String,
                val code_challenge_method: String,
            )
        }

        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val token: String,
            val code_verifier: String,
            val session_duration_minutes: Int,
        )
    }

    object Sessions {
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val session_duration_minutes: Int?,
            val session_token: String?,
            val session_jwt: String?,
        )

        @JsonClass(generateAdapter = true)
        data class RevokeRequest(
            val session_token: String?,
            val session_jwt: String?,
        )
    }

    object OTP {
        @JsonClass(generateAdapter = true)
        data class SMS(
            val phone_number: String,
            val expiration_minutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class WhatsApp(
            val phone_number: String,
            val expiration_minutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class Email(
            val email: String,
            val expiration_minutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class Authenticate(
            val token: String,
            val session_duration_minutes: Int,
        )
    }
}