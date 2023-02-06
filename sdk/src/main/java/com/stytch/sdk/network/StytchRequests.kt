package com.stytch.sdk.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal object StytchRequests {

    object MagicLinks {
        object Email {
            @JsonClass(generateAdapter = true)
            data class LoginOrCreateUserRequest(
                val email: String,
                @Json(name = "login_magic_link_url")
                val loginMagicLinkUrl: String?,
                @Json(name = "code_challenge")
                val codeChallenge: String,
                @Json(name = "code_challenge_method")
                val codeChallengeMethod: String,
                @Json(name = "login_template_id")
                val loginTemplateId: String? = null,
                @Json(name = "signup_template_id")
                val signupTemplateId: String? = null,
            )
        }

        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val token: String,
            @Json(name = "code_verifier")
            val codeVerifier: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class SendRequest(
            val email: String,
            @Json(name = "login_magic_link_url")
            val loginMagicLinkUrl: String?,
            @Json(name = "signup_magic_link_url")
            val signupMagicLinkUrl: String?,
            @Json(name = "login_expiration_minutes")
            val loginExpirationMinutes: Int?,
            @Json(name = "signup_expiration_minutes")
            val signupExpirationMinutes: Int?,
            @Json(name = "login_template_id")
            val loginTemplateId: String?,
            @Json(name = "signup_template_id")
            val signupTemplateId: String?,
            @Json(name = "code_challenge")
            val codeChallenge: String?,
        )
    }

    object Sessions {
        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int?,
        )
    }

    object Passwords {
        @JsonClass(generateAdapter = true)
        data class CreateRequest(
            val email: String,
            val password: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val email: String,
            val password: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class ResetByEmailStartRequest(
            val email: String,
            @Json(name = "code_challenge")
            val codeChallenge: String,
            @Json(name = "code_challenge_method")
            val codeChallengeMethod: String,
            @Json(name = "login_redirect_url")
            val loginRedirectUrl: String?,
            @Json(name = "login_expiration_minutes")
            val loginExpirationMinutes: Int?,
            @Json(name = "reset_password_redirect_url")
            val resetPasswordRedirectUrl: String?,
            @Json(name = "reset_password_expiration_minutes")
            val resetPasswordExpirationMinutes: Int?,
        )

        @JsonClass(generateAdapter = true)
        data class ResetByEmailRequest(
            val token: String,
            val password: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
            @Json(name = "code_verifier")
            val codeVerifier: String,
        )

        @JsonClass(generateAdapter = true)
        data class StrengthCheckRequest(
            val email: String?,
            val password: String,
        )
    }

    object OTP {
        @JsonClass(generateAdapter = true)
        data class SMS(
            @Json(name = "phone_number")
            val phoneNumber: String,
            @Json(name = "expiration_minutes")
            val expirationMinutes: Int?,
        )

        @JsonClass(generateAdapter = true)
        data class WhatsApp(
            @Json(name = "phone_number")
            val phoneNumber: String,
            @Json(name = "expiration_minutes")
            val expirationMinutes: Int?,
        )

        @JsonClass(generateAdapter = true)
        data class Email(
            val email: String,
            @Json(name = "expiration_minutes")
            val expirationMinutes: Int?,
            @Json(name = "login_template_id")
            val loginTemplateId: String?,
            @Json(name = "signup_template_id")
            val signupTemplateId: String?,
        )

        @JsonClass(generateAdapter = true)
        data class Authenticate(
            val token: String,
            @Json(name = "method_id")
            val methodId: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }

    object Biometrics {
        @JsonClass(generateAdapter = true)
        data class RegisterStartRequest(
            @Json(name = "public_key")
            val publicKey: String,
        )

        @JsonClass(generateAdapter = true)
        data class RegisterRequest(
            val signature: String,
            @Json(name = "biometric_registration_id")
            val biometricRegistrationId: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )

        @JsonClass(generateAdapter = true)
        data class AuthenticateStartRequest(
            @Json(name = "public_key")
            val publicKey: String,
        )

        @JsonClass(generateAdapter = true)
        data class AuthenticateRequest(
            val signature: String,
            @Json(name = "biometric_registration_id")
            val biometricRegistrationId: String,
            @Json(name = "session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }

    object OAuth {
        object Google {
            @JsonClass(generateAdapter = true)
            data class AuthenticateRequest(
                @Json(name = "id_token")
                val idToken: String,
                val nonce: String,
                @Json(name = "session_duration_minutes")
                val sessionDurationMinutes: Int,
            )
        }
        object ThirdParty {
            @JsonClass(generateAdapter = true)
            data class AuthenticateRequest(
                val token: String,
                @Json(name = "session_duration_minutes")
                val sessionDurationMinutes: Int,
                @Json(name = "session_custom_claims")
                val sessionCustomClaims: Map<String, Any>? = null,
                @Json(name = "session_jwt")
                val sessionJwt: String? = null,
                @Json(name = "session_token")
                val sessionToken: String? = null,
                @Json(name = "code_verifier")
                val codeVerifier: String
            )
        }
    }
}
