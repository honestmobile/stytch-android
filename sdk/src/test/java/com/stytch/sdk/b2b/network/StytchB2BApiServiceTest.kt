package com.stytch.sdk.b2b.network

import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.AuthMethods
import com.stytch.sdk.b2b.network.models.B2BRequests
import com.stytch.sdk.b2b.network.models.EmailInvites
import com.stytch.sdk.b2b.network.models.EmailJitProvisioning
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.b2b.network.models.MfaMethods
import com.stytch.sdk.b2b.network.models.MfaPolicy
import com.stytch.sdk.b2b.network.models.SearchOperator
import com.stytch.sdk.b2b.network.models.SetMFAEnrollment
import com.stytch.sdk.b2b.network.models.SsoJitProvisioning
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.utils.verifyDelete
import com.stytch.sdk.utils.verifyGet
import com.stytch.sdk.utils.verifyPost
import com.stytch.sdk.utils.verifyPut
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.EOFException
import org.junit.After
import org.junit.Before
import org.junit.Test

private const val EMAIL = "email@email.com"
private const val LOGIN_MAGIC_LINK = "loginMagicLink://"
private const val SIGNUP_MAGIC_LINK = "signupMagicLink://"

internal class StytchB2BApiServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: StytchB2BApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(12345)
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        apiService =
            ApiService.createApiService(
                mockWebServer.url("/").toString(),
                null,
                null,
                {},
                StytchB2BApiService::class.java,
            )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // region MagicLinks

    @Test
    fun `check magic links email loginOrCreate request`() {
        runBlocking {
            val parameters =
                B2BRequests.MagicLinks.Email.LoginOrSignupRequest(
                    email = EMAIL,
                    organizationId = "organizationId",
                    loginRedirectUrl = LOGIN_MAGIC_LINK,
                    signupRedirectUrl = SIGNUP_MAGIC_LINK,
                    codeChallenge = "123",
                    loginTemplateId = "loginTemplateId",
                    signupTemplateId = "signUpTemplateId",
                )
            requestIgnoringResponseException {
                apiService.loginOrSignupByEmail(parameters)
            }.verifyPost(
                expectedPath = "/b2b/magic_links/email/login_or_signup",
                expectedBody =
                    mapOf(
                        "email_address" to parameters.email,
                        "organization_id" to parameters.organizationId,
                        "login_redirect_url" to parameters.loginRedirectUrl,
                        "signup_redirect_url" to parameters.signupRedirectUrl,
                        "pkce_code_challenge" to parameters.codeChallenge,
                        "login_template_id" to parameters.loginTemplateId,
                        "signup_template_id" to parameters.signupTemplateId,
                    ),
            )
        }
    }

    @Test
    fun `check magic links email invite request`() {
        runBlocking {
            val parameters =
                B2BRequests.MagicLinks.Invite.InviteRequest(
                    emailAddress = EMAIL,
                    inviteRedirectUrl = "invite-redirect-url",
                    inviteTemplateId = "invite-template-id",
                    name = "member name",
                    untrustedMetadata = mapOf("someMetadataKey" to "someMetadataValue"),
                    locale = "en",
                    roles = listOf("role1", "role2"),
                )
            requestIgnoringResponseException {
                apiService.sendInviteMagicLink(parameters)
            }.verifyPost(
                expectedPath = "/b2b/magic_links/email/invite",
                expectedBody =
                    mapOf(
                        "email_address" to parameters.emailAddress,
                        "invite_redirect_url" to parameters.inviteRedirectUrl,
                        "invite_template_id" to parameters.inviteTemplateId,
                        "name" to parameters.name,
                        "untrusted_metadata" to parameters.untrustedMetadata,
                        "locale" to parameters.locale,
                        "roles" to parameters.roles,
                    ),
            )
        }
    }

    @Test
    fun `check magic links authenticate request`() {
        runBlocking {
            val parameters =
                B2BRequests.MagicLinks.AuthenticateRequest(
                    token = "token",
                    codeVerifier = "123",
                    sessionDurationMinutes = 60,
                )
            requestIgnoringResponseException {
                apiService.authenticate(parameters)
            }.verifyPost(
                expectedPath = "/b2b/magic_links/authenticate",
                expectedBody =
                    mapOf(
                        "magic_links_token" to parameters.token,
                        "session_duration_minutes" to parameters.sessionDurationMinutes,
                        "pkce_code_verifier" to parameters.codeVerifier,
                    ),
            )
        }
    }

    @Test
    fun `check magic links discovery send request`() {
        runBlocking {
            val parameters =
                B2BRequests.MagicLinks.Discovery.SendRequest(
                    email = "email@address.com",
                    discoveryRedirectUrl = LOGIN_MAGIC_LINK,
                    codeChallenge = "code-challenge",
                    loginTemplateId = "login-template-id",
                )
            requestIgnoringResponseException {
                apiService.sendDiscoveryMagicLink(parameters)
            }.verifyPost(
                expectedPath = "/b2b/magic_links/email/discovery/send",
                expectedBody =
                    mapOf(
                        "email_address" to parameters.email,
                        "discovery_redirect_url" to parameters.discoveryRedirectUrl,
                        "pkce_code_challenge" to parameters.codeChallenge,
                        "login_template_id" to parameters.loginTemplateId,
                    ),
            )
        }
    }

    @Test
    fun `check magic links discovery authenticate request`() {
        runBlocking {
            val parameters =
                B2BRequests.MagicLinks.Discovery.AuthenticateRequest(
                    token = "token",
                    codeVerifier = "123",
                )
            requestIgnoringResponseException {
                apiService.authenticateDiscoveryMagicLink(parameters)
            }.verifyPost(
                expectedPath = "/b2b/magic_links/discovery/authenticate",
                expectedBody =
                    mapOf(
                        "discovery_magic_links_token" to parameters.token,
                        "pkce_code_verifier" to parameters.codeVerifier,
                    ),
            )
        }
    }

    // endregion MagicLinks

    // region Sessions

    @Test
    fun `check Sessions authenticate request`() {
        runBlocking {
            val parameters = CommonRequests.Sessions.AuthenticateRequest(sessionDurationMinutes = 24)
            requestIgnoringResponseException {
                apiService.authenticateSessions(parameters)
            }.verifyPost(
                expectedPath = "/b2b/sessions/authenticate",
                expectedBody = mapOf("session_duration_minutes" to parameters.sessionDurationMinutes),
            )
        }
    }

    @Test
    fun `check Sessions revoke request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.revokeSessions()
            }.verifyPost(expectedPath = "/b2b/sessions/revoke", emptyMap())
        }
    }

    @Test
    fun `check Sessions exchange request`() {
        runBlocking {
            val parameters =
                B2BRequests.Session.ExchangeRequest(
                    organizationId = "test-123",
                    locale = "en",
                    sessionDurationMinutes = 30,
                )
            requestIgnoringResponseException {
                apiService.exchangeSession(parameters)
            }.verifyPost(
                expectedPath = "/b2b/sessions/exchange",
                expectedBody =
                    mapOf(
                        "organization_id" to parameters.organizationId,
                        "locale" to parameters.locale,
                        "session_duration_minutes" to parameters.sessionDurationMinutes,
                    ),
            )
        }
    }

    // endregion Sessions

    // region Organizations

    @Test
    fun `check Organizations getOrganizationById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.getOrganization()
            }.verifyGet("/b2b/organizations/me")
        }
    }

    @Test
    fun `Check Organizations update request`() {
        runBlocking {
            val params =
                B2BRequests.Organization.UpdateRequest(
                    organizationName = "My Cool Organization",
                    organizationSlug = "my-organization-slug",
                    organizationLogoUrl = "https://stytch.com/favicon.ico",
                    ssoDefaultConnectionId = "sso-jit-connection",
                    ssoJitProvisioning = SsoJitProvisioning.ALL_ALLOWED,
                    ssoJitProvisioningAllowedConnections = listOf("sso-jit-connection"),
                    emailAllowedDomains = listOf("stytch.com"),
                    emailJitProvisioning = EmailJitProvisioning.RESTRICTED,
                    emailInvites = EmailInvites.ALL_ALLOWED,
                    authMethods = AuthMethods.ALL_ALLOWED,
                    allowedAuthMethods = listOf(AllowedAuthMethods.GOOGLE_OAUTH, AllowedAuthMethods.SSO),
                    mfaMethods = MfaMethods.ALL_ALLOWED,
                    allowedMfaMethods = listOf(MfaMethod.SMS, MfaMethod.TOTP),
                    mfaPolicy = MfaPolicy.OPTIONAL,
                    rbacEmailImplicitRoleAssignments = listOf("rbac-role-1", "rbac-role-2"),
                )
            requestIgnoringResponseException {
                apiService.updateOrganization(params)
            }.verifyPut(
                expectedPath = "/b2b/organizations/me",
                expectedBody =
                    mapOf(
                        "organization_name" to params.organizationName,
                        "organization_slug" to params.organizationSlug,
                        "organization_logo_url" to params.organizationLogoUrl,
                        "sso_default_connection_id" to params.ssoDefaultConnectionId,
                        "sso_jit_provisioning" to params.ssoJitProvisioning,
                        "sso_jit_provisioning_allowed_connections" to params.ssoJitProvisioningAllowedConnections,
                        "email_allowed_domains" to params.emailAllowedDomains,
                        "email_jit_provisioning" to params.emailJitProvisioning,
                        "email_invites" to params.emailInvites,
                        "auth_methods" to params.authMethods,
                        "allowed_auth_methods" to params.allowedAuthMethods?.map { it.jsonName },
                        "mfa_methods" to params.mfaMethods,
                        "allowed_mfa_methods" to params.allowedMfaMethods?.map { it.jsonName },
                        "mfa_policy" to params.mfaPolicy,
                        "rbac_email_implicit_role_assignments" to params.rbacEmailImplicitRoleAssignments,
                    ),
            )
        }
    }

    @Test
    fun `Check Organization delete request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteOrganization()
            }.verifyDelete("/b2b/organizations/me")
        }
    }

    @Test
    fun `Check Organization member delete request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteOrganizationMember("my-member-id")
            }.verifyDelete("/b2b/organizations/members/my-member-id")
        }
    }

    @Test
    fun `Check organization member reactivate request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.reactivateOrganizationMember("my-member-id")
            }.verifyPut(
                expectedPath = "/b2b/organizations/members/my-member-id/reactivate",
            )
        }
    }

    @Test
    fun `check organization member deleteOrganizationMemberMFAPhoneNumber request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteOrganizationMemberMFAPhoneNumber("my-member-id")
            }.verifyDelete("/b2b/organizations/members/mfa_phone_numbers/my-member-id")
        }
    }

    @Test
    fun `check organization member deleteOrganizationMemberMFATOTP request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteOrganizationMemberMFATOTP("my-member-id")
            }.verifyDelete("/b2b/organizations/members/totp/my-member-id")
        }
    }

    @Test
    fun `check organization member deleteOrganizationMemberPassword request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteOrganizationMemberPassword("passwordId")
            }.verifyDelete("/b2b/organizations/members/passwords/passwordId")
        }
    }

    @Test
    fun `check organization member create request`() {
        runBlocking {
            val parameters =
                B2BRequests.Organization.CreateMemberRequest(
                    emailAddress = "robot@stytch.com",
                    name = "Stytch Robot",
                    isBreakGlass = true,
                    mfaEnrolled = true,
                    mfaPhoneNumber = "+15551235555",
                    untrustedMetadata = mapOf("key 1" to "value 1"),
                    createMemberAsPending = true,
                    roles = listOf("my-role", "my-other-role"),
                )
            requestIgnoringResponseException {
                apiService.createMember(parameters)
            }.verifyPost(
                expectedPath = "/b2b/organizations/members",
                expectedBody =
                    mapOf(
                        "email_address" to parameters.emailAddress,
                        "name" to parameters.name,
                        "is_breakglass" to parameters.isBreakGlass,
                        "mfa_enrolled" to parameters.mfaEnrolled,
                        "mfa_phone_number" to parameters.mfaPhoneNumber,
                        "untrusted_metadata" to parameters.untrustedMetadata,
                        "create_member_as_pending" to parameters.createMemberAsPending,
                        "roles" to parameters.roles,
                    ),
            )
        }
    }

    @Test
    fun `check organization member update request`() {
        runBlocking {
            val parameters =
                B2BRequests.Organization.UpdateMemberRequest(
                    emailAddress = "robot@stytch.com",
                    name = "Stytch Robot",
                    isBreakGlass = true,
                    mfaEnrolled = true,
                    mfaPhoneNumber = "+15551235555",
                    untrustedMetadata = mapOf("key 1" to "value 1"),
                    roles = listOf("my-role", "my-other-role"),
                    preserveExistingSessions = true,
                    defaultMfaMethod = MfaMethod.SMS,
                )
            requestIgnoringResponseException {
                apiService.updateOrganizationMember("my-member-id", parameters)
            }.verifyPut(
                expectedPath = "/b2b/organizations/members/my-member-id",
                expectedBody =
                    mapOf(
                        "email_address" to parameters.emailAddress,
                        "name" to parameters.name,
                        "is_breakglass" to parameters.isBreakGlass,
                        "mfa_enrolled" to parameters.mfaEnrolled,
                        "mfa_phone_number" to parameters.mfaPhoneNumber,
                        "untrusted_metadata" to parameters.untrustedMetadata,
                        "roles" to parameters.roles,
                        "preserve_existing_sessions" to parameters.preserveExistingSessions,
                        "default_mfa_method" to parameters.defaultMfaMethod?.jsonName,
                    ),
            )
        }
    }

    @Test
    fun `check organization member search request`() {
        runBlocking {
            val parameters =
                B2BRequests.Organization.SearchMembersRequest(
                    cursor = "1234",
                    limit = 500,
                    query =
                        B2BRequests.SearchQuery(
                            operator = SearchOperator.AND,
                            operands =
                                listOf(
                                    B2BRequests.SearchQueryOperand(
                                        filterName = "member_ids",
                                        filterValue = listOf("member-id"),
                                    ),
                                ),
                        ),
                )
            requestIgnoringResponseException {
                apiService.searchMembers(parameters)
            }.verifyPost(
                expectedPath = "/b2b/organizations/me/members/search",
                expectedBody =
                    mapOf(
                        "cursor" to parameters.cursor,
                        "limit" to parameters.limit,
                        "query" to
                            mapOf(
                                "operator" to "AND",
                                "operands" to
                                    listOf(
                                        mapOf(
                                            "filter_name" to parameters.query?.operands?.get(0)?.filterName,
                                            "filter_value" to parameters.query?.operands?.get(0)?.filterValue,
                                        ),
                                    ),
                            ),
                    ),
            )
        }
    }

    @Test
    fun `check Organizations getMember request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.getMember()
            }.verifyGet("/b2b/organizations/members/me")
        }
    }

    @Test
    fun `check Organizations updateMember request`() {
        runBlocking {
            val params =
                B2BRequests.Member.UpdateRequest(
                    name = "Stytch Robot",
                    untrustedMetadata = mapOf("a key" to "a value"),
                    mfaEnrolled = true,
                    mfaPhoneNumber = "+15005550006",
                    defaultMfaMethod = MfaMethod.SMS,
                )
            requestIgnoringResponseException {
                apiService.updateMember(params)
            }.verifyPut(
                expectedPath = "/b2b/organizations/members/update",
                expectedBody =
                    mapOf(
                        "name" to params.name,
                        "untrusted_metadata" to params.untrustedMetadata,
                        "mfa_enrolled" to params.mfaEnrolled,
                        "mfa_phone_number" to params.mfaPhoneNumber,
                        "default_mfa_method" to params.defaultMfaMethod?.jsonName,
                    ),
            )
        }
    }

    @Test
    fun `check deleteMFAPhoneNumber request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteMFAPhoneNumber()
            }.verifyDelete("/b2b/organizations/members/deletePhoneNumber")
        }
    }

    @Test
    fun `check deleteMFATOTP request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteMFATOTP()
            }.verifyDelete("/b2b/organizations/members/deleteTOTP")
        }
    }

    @Test
    fun `check deletePassword request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deletePassword("passwordId")
            }.verifyDelete("/b2b/organizations/members/passwords/passwordId")
        }
    }
    // endregion Organizations

    //region Passwords
    @Test
    fun `check Passwords authenticatePassword request`() {
        runBlocking {
            val parameters =
                B2BRequests.Passwords.AuthenticateRequest(
                    organizationId = "my-organization-id",
                    emailAddress = "my@email.address",
                    password = "my-password",
                    sessionDurationMinutes = 30,
                )
            requestIgnoringResponseException {
                apiService.authenticatePassword(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/authenticate",
                expectedBody =
                    mapOf(
                        "organization_id" to parameters.organizationId,
                        "email_address" to parameters.emailAddress,
                        "password" to parameters.password,
                        "session_duration_minutes" to parameters.sessionDurationMinutes,
                    ),
            )
        }
    }

    @Test
    fun `check Passwords resetPasswordByEmailStart request`() {
        runBlocking {
            val parameters =
                B2BRequests.Passwords.ResetByEmailStartRequest(
                    organizationId = "my-organization-id",
                    emailAddress = "my@email.address",
                    codeChallenge = "code-challenge-string",
                    loginRedirectUrl = "login://redirect",
                    resetPasswordExpirationMinutes = 30,
                    resetPasswordTemplateId = "reset-password-template-id",
                    resetPasswordRedirectUrl = "reset://redirect",
                )
            requestIgnoringResponseException {
                apiService.resetPasswordByEmailStart(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/email/reset/start",
                expectedBody =
                    mapOf(
                        "organization_id" to parameters.organizationId,
                        "email_address" to parameters.emailAddress,
                        "code_challenge" to parameters.codeChallenge,
                        "login_redirect_url" to parameters.loginRedirectUrl,
                        "reset_password_expiration_minutes" to parameters.resetPasswordExpirationMinutes,
                        "reset_password_template_id" to parameters.resetPasswordTemplateId,
                        "reset_password_redirect_url" to parameters.resetPasswordRedirectUrl,
                    ),
            )
        }
    }

    @Test
    fun `check Passwords resetPasswordByEmail request`() {
        runBlocking {
            val parameters =
                B2BRequests.Passwords.ResetByEmailRequest(
                    passwordResetToken = "password-reset-token",
                    password = "my-password",
                    sessionDurationMinutes = 30,
                    codeVerifier = "code-verifier",
                )
            requestIgnoringResponseException {
                apiService.resetPasswordByEmail(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/email/reset",
                expectedBody =
                    mapOf(
                        "password_reset_token" to parameters.passwordResetToken,
                        "password" to parameters.password,
                        "session_duration_minutes" to parameters.sessionDurationMinutes,
                        "code_verifier" to parameters.codeVerifier,
                    ),
            )
        }
    }

    @Test
    fun `check Passwords resetPasswordByExisting request`() {
        runBlocking {
            val parameters =
                B2BRequests.Passwords.ResetByExistingPasswordRequest(
                    organizationId = "my-organization-id",
                    emailAddress = "my@email.address",
                    existingPassword = "existing-password",
                    newPassword = "new-password",
                    sessionDurationMinutes = 30,
                )
            requestIgnoringResponseException {
                apiService.resetPasswordByExisting(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/existing_password/reset",
                expectedBody =
                    mapOf(
                        "organization_id" to parameters.organizationId,
                        "email_address" to parameters.emailAddress,
                        "existing_password" to parameters.existingPassword,
                        "new_password" to parameters.newPassword,
                        "session_duration_minutes" to parameters.sessionDurationMinutes,
                    ),
            )
        }
    }

    @Test
    fun `check Passwords resetPasswordBySession request`() {
        runBlocking {
            val parameters =
                B2BRequests.Passwords.ResetBySessionRequest(
                    organizationId = "my-organization-id",
                    password = "my-password",
                )
            requestIgnoringResponseException {
                apiService.resetPasswordBySession(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/session/reset",
                expectedBody =
                    mapOf(
                        "organization_id" to parameters.organizationId,
                        "password" to parameters.password,
                    ),
            )
        }
    }

    @Test
    fun `check Passwords passwordStrengthCheck request`() {
        runBlocking {
            val parameters =
                B2BRequests.Passwords.StrengthCheckRequest(
                    email = "my@email.address",
                    password = "my-password",
                )
            requestIgnoringResponseException {
                apiService.passwordStrengthCheck(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/strength_check",
                expectedBody =
                    mapOf(
                        "email" to parameters.email,
                        "password" to parameters.password,
                    ),
            )
        }
    }
    //endregion Passwords

    //region Discovery
    @Test
    fun `check Discovery discoverOrganizations request`() {
        runBlocking {
            val parameters =
                B2BRequests.Discovery.MembershipsRequest(
                    intermediateSessionToken = "intermediate-session-token",
                )
            requestIgnoringResponseException {
                apiService.discoverOrganizations(parameters)
            }.verifyPost(
                expectedPath = "/b2b/discovery/organizations",
                expectedBody =
                    mapOf(
                        "intermediate_session_token" to parameters.intermediateSessionToken,
                    ),
            )
        }
    }

    @Test
    fun `check Discovery intermediateSessionExchange request`() {
        runBlocking {
            val parameters =
                B2BRequests.Discovery.SessionExchangeRequest(
                    intermediateSessionToken = "intermediate-session-token",
                    organizationId = "organization-id",
                    sessionDurationMinutes = 30,
                )
            requestIgnoringResponseException {
                apiService.intermediateSessionExchange(parameters)
            }.verifyPost(
                expectedPath = "/b2b/discovery/intermediate_sessions/exchange",
                expectedBody =
                    mapOf(
                        "intermediate_session_token" to parameters.intermediateSessionToken,
                        "organization_id" to parameters.organizationId,
                        "session_duration_minutes" to parameters.sessionDurationMinutes,
                    ),
            )
        }
    }

    @Test
    fun `check Discovery createOrganization request`() {
        runBlocking {
            val parameters =
                B2BRequests.Discovery.CreateRequest(
                    intermediateSessionToken = "intermediate-session-token",
                    organizationName = "organization-name",
                    organizationSlug = "organization-slug",
                    organizationLogoUrl = "organization-logo-url",
                    sessionDurationMinutes = 30,
                    ssoJitProvisioning = SsoJitProvisioning.ALL_ALLOWED,
                    emailAllowedDomains = listOf("alloweddomain.com"),
                    emailInvites = EmailInvites.NOT_ALLOWED,
                    emailJitProvisioning = EmailJitProvisioning.RESTRICTED,
                    authMethods = AuthMethods.RESTRICTED,
                    allowedAuthMethods =
                        listOf(
                            AllowedAuthMethods.MAGIC_LINK,
                            AllowedAuthMethods.PASSWORD,
                        ),
                )
            requestIgnoringResponseException {
                apiService.createOrganization(parameters)
            }.verifyPost(
                expectedPath = "/b2b/discovery/organizations/create",
                expectedBody =
                    mapOf(
                        "intermediate_session_token" to parameters.intermediateSessionToken,
                        "organization_name" to parameters.organizationName,
                        "organization_slug" to parameters.organizationSlug,
                        "organization_logo_url" to parameters.organizationLogoUrl,
                        "session_duration_minutes" to parameters.sessionDurationMinutes,
                        "sso_jit_provisioning" to parameters.ssoJitProvisioning,
                        "email_allowed_domains" to parameters.emailAllowedDomains,
                        "email_jit_provisioning" to parameters.emailJitProvisioning,
                        "email_invites" to parameters.emailInvites,
                        "auth_methods" to parameters.authMethods,
                        "allowed_auth_methods" to parameters.allowedAuthMethods?.map { it.jsonName },
                    ),
            )
        }
    }
    //endregion Discovery

    //region SSO
    @Test
    fun `check SSO authenticate request`() {
        runBlocking {
            val parameters =
                B2BRequests.SSO.AuthenticateRequest(
                    ssoToken = "sso-token",
                    sessionDurationMinutes = 30,
                    codeVerifier = "code-verifier",
                )
            requestIgnoringResponseException {
                apiService.ssoAuthenticate(parameters)
            }.verifyPost(
                expectedPath = "/b2b/sso/authenticate",
                expectedBody =
                    mapOf(
                        "sso_token" to parameters.ssoToken,
                        "session_duration_minutes" to parameters.sessionDurationMinutes,
                        "pkce_code_verifier" to parameters.codeVerifier,
                    ),
            )
        }
    }
    //endregion

    // region Bootstrap
    @Test
    fun `check getBootstrapData request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.getBootstrapData("mock-public-token")
            }.verifyGet("/projects/bootstrap/mock-public-token")
        }
    }
    // endregion Bootstrap

    //region Events
    @Test
    fun `check Events logEvent request`() {
        runBlocking {
            val parameters: CommonRequests.Events.Event =
                CommonRequests.Events.Event(
                    telemetry =
                        CommonRequests.Events.EventTelemetry(
                            eventId = "event-id",
                            appSessionId = "app-session-id",
                            persistentId = "persistent-id",
                            clientSentAt = "client sent at",
                            timezone = "timezone",
                            app =
                                CommonRequests.Events.VersionIdentifier(
                                    identifier = "app-id",
                                    version = "app-version",
                                ),
                            os =
                                CommonRequests.Events.VersionIdentifier(
                                    identifier = "os-id",
                                    version = "os-version",
                                ),
                            sdk =
                                CommonRequests.Events.VersionIdentifier(
                                    identifier = "sdk-id",
                                    version = "sdk-version",
                                ),
                            device =
                                CommonRequests.Events.DeviceIdentifier(
                                    model = "device-model",
                                    screenSize = "screen-size",
                                ),
                        ),
                    event =
                        CommonRequests.Events.EventEvent(
                            publicToken = "public-token",
                            eventName = "event name",
                            details = mapOf("test-key" to "test value"),
                        ),
                )
            requestIgnoringResponseException {
                apiService.logEvent(listOf(parameters))
            }.verifyPost(
                expectedPath = "/events",
                expectedBody =
                    listOf(
                        mapOf(
                            "telemetry" to
                                mapOf(
                                    "event_id" to parameters.telemetry.eventId,
                                    "app_session_id" to parameters.telemetry.appSessionId,
                                    "persistent_id" to parameters.telemetry.persistentId,
                                    "client_sent_at" to parameters.telemetry.clientSentAt,
                                    "timezone" to parameters.telemetry.timezone,
                                    "app" to
                                        mapOf(
                                            "identifier" to parameters.telemetry.app.identifier,
                                            "version" to parameters.telemetry.app.version,
                                        ),
                                    "sdk" to
                                        mapOf(
                                            "identifier" to parameters.telemetry.sdk.identifier,
                                            "version" to parameters.telemetry.sdk.version,
                                        ),
                                    "os" to
                                        mapOf(
                                            "identifier" to parameters.telemetry.os.identifier,
                                            "version" to parameters.telemetry.os.version,
                                        ),
                                    "device" to
                                        mapOf(
                                            "model" to parameters.telemetry.device.model,
                                            "screen_size" to parameters.telemetry.device.screenSize,
                                        ),
                                ),
                            "event" to
                                mapOf(
                                    "public_token" to parameters.event.publicToken,
                                    "event_name" to parameters.event.eventName,
                                    "details" to parameters.event.details,
                                ),
                        ),
                    ),
            )
        }
    }
    //endregion Events

    //region OTP
    @Test
    fun `check OTP SMS send request`() {
        runBlocking {
            val parameters =
                B2BRequests.OTP.SMS.SendRequest(
                    organizationId = "my-organization-id",
                    memberId = "my-member-id",
                    mfaPhoneNumber = "+15555550123",
                    locale = "en",
                )
            requestIgnoringResponseException {
                apiService.sendSMSOTP(parameters)
            }.verifyPost(
                expectedPath = "/b2b/otps/sms/send",
                expectedBody =
                    mapOf(
                        "organization_id" to parameters.organizationId,
                        "member_id" to parameters.memberId,
                        "mfa_phone_number" to parameters.mfaPhoneNumber,
                        "locale" to parameters.locale,
                    ),
            )
        }
    }

    @Test
    fun `check OTP SMS authenticate request`() {
        runBlocking {
            val parameters =
                B2BRequests.OTP.SMS.AuthenticateRequest(
                    organizationId = "my-organization-id",
                    memberId = "my-member-id",
                    code = "012345",
                    sessionDurationMinutes = 30,
                )
            requestIgnoringResponseException {
                apiService.authenticateSMSOTP(parameters)
            }.verifyPost(
                expectedPath = "/b2b/otps/sms/authenticate",
                expectedBody =
                    mapOf(
                        "organization_id" to parameters.organizationId,
                        "member_id" to parameters.memberId,
                        "code" to parameters.code,
                        "session_duration_minutes" to parameters.sessionDurationMinutes,
                    ),
            )
        }
    }
    //endregion OTP

    //region TOTP
    @Test
    fun `check TOTP create request`() {
        runBlocking {
            val parameters =
                B2BRequests.TOTP.CreateRequest(
                    organizationId = "my-organization-id",
                    memberId = "my-member-id",
                    expirationMinutes = 30,
                )
            requestIgnoringResponseException {
                apiService.createTOTP(parameters)
            }.verifyPost(
                expectedPath = "/b2b/totp",
                expectedBody =
                    mapOf(
                        "organization_id" to parameters.organizationId,
                        "member_id" to parameters.memberId,
                        "expiration_minutes" to parameters.expirationMinutes,
                    ),
            )
        }
    }

    @Test
    fun `check TOTP authenticate request`() {
        runBlocking {
            val parameters =
                B2BRequests.TOTP.AuthenticateRequest(
                    organizationId = "my-organization-id",
                    memberId = "my-member-id",
                    code = "code",
                    setMFAEnrollment = SetMFAEnrollment.ENROLL,
                    setDefaultMfaMethod = true,
                    sessionDurationMinutes = 30,
                )
            requestIgnoringResponseException {
                apiService.authenticateTOTP(parameters)
            }.verifyPost(
                expectedPath = "/b2b/totp/authenticate",
                expectedBody =
                    mapOf(
                        "organization_id" to parameters.organizationId,
                        "member_id" to parameters.memberId,
                        "code" to parameters.code,
                        "session_duration_minutes" to parameters.sessionDurationMinutes,
                        "set_mfa_enrollment" to parameters.setMFAEnrollment?.jsonName,
                        "set_default_mfa" to parameters.setDefaultMfaMethod,
                    ),
            )
        }
    }
    //endregion TOTP

    private suspend fun requestIgnoringResponseException(block: suspend () -> Unit): RecordedRequest {
        try {
            block()
        } catch (_: EOFException) {
            // OkHTTP throws EOFException because it expects a response body, but we're intentionally not creating them
        }
        return mockWebServer.takeRequest()
    }
}
