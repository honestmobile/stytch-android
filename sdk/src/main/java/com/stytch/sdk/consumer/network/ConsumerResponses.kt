package com.stytch.sdk.consumer.network

import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.network.LoginOrCreateOTPData
import com.stytch.sdk.common.network.StytchDataResponse

internal object ConsumerResponses {
    object Passwords {

        @JsonClass(generateAdapter = true)
        class PasswordsCreateResponse(data: CreateResponse) : StytchDataResponse<CreateResponse>(data)
    }

    object Biometrics {
        @JsonClass(generateAdapter = true)
        class RegisterResponse(data: BiometricsAuthData) :
            StytchDataResponse<BiometricsAuthData>(data)

        @JsonClass(generateAdapter = true)
        class AuthenticateResponse(data: BiometricsAuthData) :
            StytchDataResponse<BiometricsAuthData>(data)
    }

    object User {
        @JsonClass(generateAdapter = true)
        class UserResponse(data: UserData) : StytchDataResponse<UserData>(data)

        @JsonClass(generateAdapter = true)
        class DeleteFactorResponse(data: DeleteAuthenticationFactorData) :
            StytchDataResponse<DeleteAuthenticationFactorData>(data)
    }

    object OAuth {
        @JsonClass(generateAdapter = true)
        class OAuthAuthenticateResponse(data: OAuthData) : StytchDataResponse<OAuthData>(data)
    }

    @JsonClass(generateAdapter = true)
    class AuthenticateResponse(data: AuthData) : StytchDataResponse<AuthData>(data)

    @JsonClass(generateAdapter = true)
    class LoginOrCreateOTPResponse(data: LoginOrCreateOTPData) : StytchDataResponse<LoginOrCreateOTPData>(data)
}
