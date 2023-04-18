package com.stytch.sdk.b2b.network

import com.stytch.sdk.b2b.network.models.B2BRequests
import com.stytch.sdk.b2b.network.models.B2BResponses
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.common.network.models.CommonResponses
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface StytchB2BApiService : ApiService {
    //region MagicLinks
    @POST("b2b/magic_links/email/login_or_signup")
    suspend fun loginOrSignupByEmail(
        @Body request: B2BRequests.MagicLinks.Email.LoginOrSignupRequest
    ): CommonResponses.MagicLinks.Email.LoginOrCreateUserResponse

    @POST("b2b/magic_links/authenticate")
    suspend fun authenticate(
        @Body request: B2BRequests.MagicLinks.AuthenticateRequest
    ): B2BResponses.MagicLinks.AuthenticateResponse
    //endregion Magic Links

    //region Sessions
    @POST("b2b/sessions/authenticate")
    suspend fun authenticateSessions(
        @Body request: CommonRequests.Sessions.AuthenticateRequest
    ): B2BResponses.Sessions.AuthenticateResponse

    @POST("b2b/sessions/revoke")
    suspend fun revokeSessions(): CommonResponses.Sessions.RevokeResponse
    //endregion Sessions

    //region Organizations
    @GET("b2b/organizations/me")
    suspend fun getOrganization(): B2BResponses.Organizations.GetOrganizationResponse

    @GET("b2b/organizations/members/me")
    suspend fun getMember(): B2BResponses.Organizations.GetMemberResponse
    //endregion Organizations

    //region Passwords
    @POST("b2b/passwords/authenticate")
    suspend fun authenticatePassword(
        @Body request: B2BRequests.Passwords.AuthenticateRequest
    ): B2BResponses.Passwords.AuthenticateResponse

    @POST("b2b/passwords/email/reset/start")
    suspend fun resetPasswordByEmailStart(
        @Body request: B2BRequests.Passwords.ResetByEmailStartRequest
    ): B2BResponses.Passwords.ResetByEmailStartResponse

    @POST("b2b/passwords/email/reset")
    suspend fun resetPasswordByEmail(
        @Body request: B2BRequests.Passwords.ResetByEmailRequest
    ): B2BResponses.Passwords.ResetByEmailResponse

    @POST("b2b/passwords/existing_password/reset")
    suspend fun resetPasswordByExisting(
        @Body request: B2BRequests.Passwords.ResetByExistingPasswordRequest
    ): B2BResponses.Passwords.ResetByExistingPasswordResponse

    @POST("b2b/passwords/session/reset")
    suspend fun resetPasswordBySession(
        @Body request: B2BRequests.Passwords.ResetBySessionRequest
    ): B2BResponses.Passwords.ResetBySessionResponse

    @POST("b2b/passwords/strength_check")
    suspend fun passwordStrengthCheck(
        @Body request: B2BRequests.Passwords.StrengthCheckRequest
    ): B2BResponses.Passwords.StrengthCheckResponse
    //endregion Passwords
}
