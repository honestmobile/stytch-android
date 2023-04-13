package com.stytch.sdk.consumer.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.models.AuthenticationFactor
import com.stytch.sdk.common.network.models.BiometricRegistrations
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import com.stytch.sdk.common.network.models.CryptoWalletData
import com.stytch.sdk.common.network.models.EmailData
import com.stytch.sdk.common.network.models.NameData
import com.stytch.sdk.common.network.models.Password
import com.stytch.sdk.common.network.models.PhoneNumber
import com.stytch.sdk.common.network.models.Provider
import com.stytch.sdk.common.network.models.TOTP
import com.stytch.sdk.common.network.models.WebAuthNRegistrations

public interface IAuthData : CommonAuthenticationData {
    public val session: SessionData
    public override val sessionJwt: String
    public override val sessionToken: String
    public val user: UserData
}

public interface INativeOAuthData : IAuthData {
    public val userCreated: Boolean
}

@JsonClass(generateAdapter = true)
public data class AuthData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
) : IAuthData

@JsonClass(generateAdapter = true)
public data class SessionData(
    val attributes: AttributesData,
    @Json(name = "custom_claims")
    val customClaims: Map<String, String>?,
    @Json(name = "expires_at")
    val expiresAt: String,
    @Json(name = "last_accessed_at")
    val lastAccessedAt: String,
    @Json(name = "session_id")
    val sessionId: String,
    @Json(name = "started_at")
    val startedAt: String,
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "authentication_factors")
    val authenticationFactors: List<AuthenticationFactor>,
) {
    @JsonClass(generateAdapter = true)
    public data class AttributesData(
        @Json(name = "ip_address")
        val ipAddress: String,
        @Json(name = "user_agent")
        val userAgent: String,
    )
}

@JsonClass(generateAdapter = true)
public data class UserData(
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "crypto_wallets")
    val cryptoWallets: List<CryptoWalletData>,
    val emails: List<EmailData>,
    val name: NameData,
    val password: Password?,
    @Json(name = "phone_numbers")
    val phoneNumbers: List<PhoneNumber>,
    val providers: List<Provider>,
    @Json(name = "request_id")
    val requestId: String?,
    val status: String,
    @Json(name = "status_code")
    val statusCode: Int?,
    val totps: List<TOTP>,
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "webauthn_registrations")
    val webauthnRegistrations: List<WebAuthNRegistrations>,
    @Json(name = "biometric_registrations")
    val biometricRegistrations: List<BiometricRegistrations>
)

@JsonClass(generateAdapter = true)
public data class CreateResponse(
    @Json(name = "method_id")
    val methodId: String?,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "reset_sessions")
    val resetSessions: Boolean?,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
    @Json(name = "email_id")
    val emailId: String,
    @Json(name = "user_id")
    val userId: String,
) : IAuthData

@JsonClass(generateAdapter = true)
public data class BiometricsAuthData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
    @Json(name = "biometric_registration_id")
    val biometricRegistrationId: String,
) : IAuthData

@JsonClass(generateAdapter = true)
public data class DeleteAuthenticationFactorData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val user: UserData
)

@JsonClass(generateAdapter = true)
public data class OAuthData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
    @Json(name = "oauth_user_registration_id")
    val oauthUserRegistrationId: String,
    @Json(name = "provider_subject")
    val providerSubject: String,
    @Json(name = "provider_type")
    val providerType: String,
    @Json(name = "provider_values")
    val providerValues: OAuthProviderValues
) : IAuthData {
    @JsonClass(generateAdapter = true)
    public data class OAuthProviderValues(
        @Json(name = "access_token")
        val accessToken: String,
        @Json(name = "refresh_token")
        val refreshToken: String,
        @Json(name = "id_token")
        val idToken: String,
        val scopes: List<String>,
    )
}

@JsonClass(generateAdapter = true)
public data class NativeOAuthData(
    override val session: SessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val user: UserData,
    @Json(name = "user_created")
    override val userCreated: Boolean
) : INativeOAuthData
