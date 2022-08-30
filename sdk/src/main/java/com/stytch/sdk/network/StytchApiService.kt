package com.stytch.sdk.network

import com.stytch.sdk.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface StytchApiService {

    //region Magic Links
    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest,
    ): StytchResponses.MagicLinks.Email.LoginOrCreateUserResponse

    @POST("magic_links/authenticate")
    suspend fun authenticate(
        @Body request: StytchRequests.MagicLinks.AuthenticateRequest,
    ): StytchResponses.AuthenticateResponse
    //endregion Magic Links

    //region Sessions
    @POST("sessions/authenticate")
    suspend fun authenticateSessions(@Body request: StytchRequests.Sessions.AuthenticateRequest): StytchResponses.AuthenticateResponse

    @POST("sessions/revoke")
    suspend fun revokeSessions(): StytchResponses.Sessions.RevokeResponse
    //endregion Sessions

    //region OTP
    @POST("otps/sms/login_or_create")
    suspend fun loginOrCreateUserByOTPWithSMS(
        @Body request: StytchRequests.OTP.SMS,
    ): StytchResponses.BasicResponse

    @POST("otps/whatsapp/login_or_create")
    suspend fun loginOrCreateUserByOTPWithWhatsApp(
        @Body request: StytchRequests.OTP.WhatsApp,
    ): StytchResponses.BasicResponse

    @POST("otps/email/login_or_create")
    suspend fun loginOrCreateUserByOTPWithEmail(
        @Body request: StytchRequests.OTP.Email,
    ): StytchResponses.BasicResponse

    @POST("otps/authenticate") // TODO Need to create a proper name to differentiate fom magiclinks authenticate
    suspend fun authenticateWithOTP(
        @Body request: StytchRequests.OTP.Authenticate,
    ): StytchResponses.AuthenticateResponse
    //endregionOTP

    //region passwords
    @POST("passwords")
    suspend fun passwords(
        @Body request: StytchRequests.OTP.SMS,
    ): StytchResponses.Passwords.CreateResponse

    @POST("passwords/authenticate")
    suspend fun authenticateWithPasswords(
        @Body request: StytchRequests.Passwords.AuthenticateRequest,
    ): StytchResponses.AuthenticateResponse

    @POST("passwords/email/reset/start")
    suspend fun resetByEmailStart(
        @Body request: StytchRequests.Passwords.ResetByEmailStartRequest,
    ): BaseResponse

    @POST("passwords/email/reset")
    suspend fun resetByEmail(
        @Body request: StytchRequests.Passwords.RestByEmailRequest,
    ): StytchResponses.AuthenticateResponse

    @POST("passwords/strength_check")
    suspend fun strengthCheck(
        @Body request: StytchRequests.Passwords.StrengthCheckRequest,
    ): StytchResponses.Passwords.StrengthCheckResponse
    //endregion passwords
}
