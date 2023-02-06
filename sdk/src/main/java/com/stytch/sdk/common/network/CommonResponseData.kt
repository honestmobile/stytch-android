package com.stytch.sdk.common.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class BasicData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
)

@JsonClass(generateAdapter = true)
public data class StytchErrorResponse(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "error_type")
    val errorType: String,
    @Json(name = "error_message")
    val errorMessage: String?,
    @Json(name = "error_url")
    val errorUrl: String,
)

@JsonClass(generateAdapter = true)
public data class CryptoWalletData(
    // The id of the crypto wallet.
    @Json(name = "crypto_wallet_id")
    val id: String,

    // The address of the cryptowallet.
    @Json(name = "crypto_wallet_address")
    val address: String,
    @Json(name = "crypto_wallet_type")
    val walletType: String,
    // The verification status of the cryptowallet.
    val verified: Boolean,
)

@JsonClass(generateAdapter = true)
public data class EmailData(
    public val email: String,
    @Json(name = "email_id")
    public val emailId: String,
    public val verified: Boolean,
)

@JsonClass(generateAdapter = true)
public data class NameData(
    @Json(name = "first_name")
    val firstName: String?,
    @Json(name = "last_name")
    val lastName: String?,
    @Json(name = "middle_name")
    val middleName: String?,
)

@JsonClass(generateAdapter = true)
public data class WebAuthNRegistrations(
    // The domain of the WebAuthN registration.
    val domain: String,
    // The user agent of the registration.
    val userAgent: String,
    // The verification status of the registration.
    val verified: Boolean,
    // The id of the registration.
    @Json(name = "webauthn_registration_id")
    val id: String,
)

@JsonClass(generateAdapter = true)
public data class PhoneNumber(
    // The phone number.
    @Json(name = "phone_number")
    val phoneNumber: String,
    // The id of the phone number.
    @Json(name = "phone_id")
    val id: String,
    // The verification status of the phone number.
    val verified: Boolean,
)

@JsonClass(generateAdapter = true)
public data class Provider(
    // The subject of the provider.
    @Json(name = "provider_subject")
    val providerSubject: String,
    // The type of the provider.
    @Json(name = "provider_type")
    val providerType: String,
)

@JsonClass(generateAdapter = true)
public data class TOTP(
    // The id of the TOTP.
    @Json(name = "totpId")
    var id: String,
    // The verification status of the TOTP.
    val verified: Boolean,
)

@JsonClass(generateAdapter = true)
public data class BiometricRegistrations(
    // The id of the biometric registration
    @Json(name = "biometric_registration_id")
    val id: String,
    // The verification status of the biometric registration.
    val verified: Boolean,
)

@JsonClass(generateAdapter = true)
public data class Password(
    @Json(name = "password_id")
    val passwordId: String,
    @Json(name = "requires_reset")
    val requiresReset: Boolean,
)

@JsonClass(generateAdapter = true)
public data class Feedback(
    val suggestions: List<String>,
    val warning: String,
)

@JsonClass(generateAdapter = true)
public data class StrengthCheckResponse(
    @Json(name = "breached_password")
    val breachedPassword: Boolean,
    val feedback: Feedback,
    @Json(name = "request_id")
    val requestId: String,
    val score: Int,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "valid_password")
    val validPassword: Boolean,
)

@JsonClass(generateAdapter = true)
public data class BiometricsStartResponse(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "biometric_registration_id")
    val biometricRegistrationId: String,
    val challenge: String,
)

@JsonClass(generateAdapter = true)
public data class LoginOrCreateOTPData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "method_id")
    val methodId: String,
)
