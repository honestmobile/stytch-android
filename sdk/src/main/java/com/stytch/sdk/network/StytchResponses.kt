package com.stytch.sdk.network

import com.squareup.moshi.JsonClass
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sdk.network.responseData.BasicData
import com.stytch.sdk.network.responseData.CreateResponse
import com.stytch.sdk.network.responseData.StrengthCheckResponse
import com.stytch.sdk.network.responseData.UserData

internal object StytchResponses {

    object MagicLinks {
        object Email {
            @JsonClass(generateAdapter = true)
            class LoginOrCreateUserResponse(data: BasicData) : StytchDataResponse<BasicData>(data)
        }
    }

    object Sessions {
        @JsonClass(generateAdapter = true)
        class RevokeResponse(data: BasicData) : StytchDataResponse<BasicData>(data)
    }

    object Passwords {

        @JsonClass(generateAdapter = true)
        class PasswordsCreateResponse(data: CreateResponse) : StytchDataResponse<CreateResponse>(data)

        @JsonClass(generateAdapter = true)
        class PasswordsStrengthCheckResponse(data: StrengthCheckResponse): StytchDataResponse<StrengthCheckResponse>(data)
    }

    object User{
        @JsonClass(generateAdapter = true)
        class UserResponse(data: UserData) : StytchDataResponse<UserData>(data)
    }

    @JsonClass(generateAdapter = true)
    class BasicResponse(data: BasicData) : StytchDataResponse<BasicData>(data)

    @JsonClass(generateAdapter = true)
    class AuthenticateResponse(data: AuthData) : StytchDataResponse<AuthData>(data)

    open class StytchDataResponse<T>(
        val data: T,
    )
}