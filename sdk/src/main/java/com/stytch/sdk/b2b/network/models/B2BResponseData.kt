package com.stytch.sdk.b2b.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.models.AuthenticationFactor
import com.stytch.sdk.common.network.models.CommonAuthenticationData

public interface IB2BAuthData : CommonAuthenticationData {
    public val memberSession: B2BSessionData
    public override val sessionJwt: String
    public override val sessionToken: String
    public val member: MemberData
    public val organization: Organization
}

@JsonClass(generateAdapter = true)
public data class B2BAuthData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val member: MemberData,
    override val organization: Organization
) : IB2BAuthData

@JsonClass(generateAdapter = true)
public data class B2BEMLAuthenticateData(
    override val member: MemberData,
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "method_id")
    val methodId: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData,
    @Json(name = "reset_sessions")
    val resetSessions: Boolean,
    override val organization: Organization
) : IB2BAuthData

@JsonClass(generateAdapter = true)
public data class B2BSessionData(
    @Json(name = "member_session_id")
    val memberSessionId: String,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "started_at")
    val startedAt: String,
    @Json(name = "last_accessed_at")
    val lastAccessedAt: String,
    @Json(name = "expires_at")
    val expiresAt: String,
    @Json(name = "authentication_factors")
    val authenticationFactors: List<AuthenticationFactor>,
    @Json(name = "custom_claims")
    val customClaims: Map<String, Any?>?,
)

@JsonClass(generateAdapter = true)
public data class MemberResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val member: MemberData,
)

@JsonClass(generateAdapter = true)
public data class MemberData(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "email_address")
    val email: String,
    val status: String,
    val name: String,
    @Json(name = "trusted_metadata")
    val trustedMetadata: Map<String, Any?>?,
    @Json(name = "untrusted_metadata")
    val untrustedMetadata: Map<String, Any?>?,
    @Json(name = "sso_registrations")
    val ssoRegistrations: List<SSORegistration>
)

@JsonClass(generateAdapter = true)
public data class SSORegistration(
    @Json(name = "connection_id")
    val connectionId: String,
    @Json(name = "external_id")
    val externalId: String,
    @Json(name = "registration_id")
    val registrationId: String,
    @Json(name = "sso_attributes")
    val ssoAttributes: Map<String, Any?>?,
)

@JsonClass(generateAdapter = true)
public data class OrganizationResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    val organization: Organization,
)

@JsonClass(generateAdapter = true)
public data class Organization(
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "organization_name")
    val organizatioName: String,
    @Json(name = "organization_slug")
    val organizationSlug: String,
    @Json(name = "organization_logo_url")
    val organizationLogoUrl: String,
    @Json(name = "trusted_metadata")
    val trustedMetadata: Map<String, Any?>,
)

@JsonClass(generateAdapter = true)
public data class PasswordsAuthenticateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val member: MemberData,
    override val organization: Organization,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "organization_id")
    val organizationId: String,
) : IB2BAuthData

@JsonClass(generateAdapter = true)
public data class EmailResetResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    override val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    override val member: MemberData,
    override val organization: Organization,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "member_email_id")
    val memberEmailId: String,
) : IB2BAuthData

@JsonClass(generateAdapter = true)
public data class SessionResetResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_id")
    val memberId: String,
    @Json(name = "member_session")
    val memberSession: B2BSessionData,
    val member: MemberData,
    val organization: Organization,
)

@JsonClass(generateAdapter = true)
public data class StrengthCheckResponseData(
    @Json(name = "valid_password")
    val validPassword: Boolean,
    val score: Int,
    @Json(name = "breached_password")
    val breachedPassword: Boolean,
    @Json(name = "strength_policy")
    val strengthPolicy: String,
    @Json(name = "breach_detection_on_create")
    val breachDetectionOnCreate: Boolean,
    @Json(name = "zxcvbn_feedback")
    val zxcvbnFeedback: ZXCVBNFeedback,
    @Json(name = "luds_feedback")
    val ludsFeedback: LUDSFeedback,
) {
    @JsonClass(generateAdapter = true)
    public data class ZXCVBNFeedback(
        val suggestions: List<String>,
        val warning: String,
    )

    @JsonClass(generateAdapter = true)
    public data class LUDSFeedback(
        @Json(name = "has_lower_case")
        val hasLowerCase: Boolean,
        @Json(name = "has_upper_case")
        val hasUpperCase: Boolean,
        @Json(name = "has_digit")
        val hasDigit: Boolean,
        @Json(name = "has_symbol")
        val hasSymbol: Boolean,
        @Json(name = "missing_complexity")
        val missingComplexity: Int,
        @Json(name = "missing_characters")
        val missingCharacters: Int,
    )
}

@JsonClass(generateAdapter = true)
public data class DiscoveredOrganizationsResponseData(
    @Json(name = "email_address")
    val emailAddress: String,
    @Json(name = "discovered_organizations")
    val discoveredOrganizations: List<DiscoveredOrganization>
)

@JsonClass(generateAdapter = true)
public data class DiscoveredOrganization(
    val organization: Organization,
    val membership: Membership,
    @Json(name = "member_authenticated")
    val memberAuthenticated: Boolean,
)

@JsonClass(generateAdapter = true)
public data class Membership(
    val type: String,
    val details: MembershipDetails?,
    val member: MemberData?,
)

@JsonClass(generateAdapter = true)
public data class MembershipDetails(
    val domain: String,
)

@JsonClass(generateAdapter = true)
public data class IntermediateSessionExchangeResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    val sessionJwt: String,
    @Json(name = "session_token")
    val sessionToken: String,
)

@JsonClass(generateAdapter = true)
public data class OrganizationCreateResponseData(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "request_id")
    val requestId: String,
    @Json(name = "member_session")
    val memberSession: B2BSessionData,
    @Json(name = "session_jwt")
    val sessionJwt: String,
    @Json(name = "session_token")
    val sessionToken: String,
)

@JsonClass(generateAdapter = true)
public data class DiscoveryAuthenticateResponseData(
    @Json(name = "intermediate_session_token")
    val intermediateSessionToken: String,
    @Json(name = "email_address")
    val emailAddress: String,
    @Json(name = "discovered_organizations")
    val discoveredOrganizations: List<DiscoveredOrganization>
)

@JsonClass(generateAdapter = true)
public data class SSOAuthenticateResponseData(
    @Json(name = "member_id")
    val memberId: String,
    override val member: MemberData,
    @Json(name = "organization_id")
    val organizationId: String,
    @Json(name = "method_id")
    val methodId: String,
    @Json(name = "session_jwt")
    override val sessionJwt: String,
    @Json(name = "session_token")
    override val sessionToken: String,
    @Json(name = "session")
    override val memberSession: B2BSessionData,
    @Json(name = "reset_session")
    val resetSession: Boolean,
    override val organization: Organization
) : IB2BAuthData
