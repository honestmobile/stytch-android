package com.stytch.sdk

public interface OTP {

    public data class AuthParameters(
        val token: String,
        val sessionDurationMinutes: UInt = 60u,
    )

    public val sms: SmsOTP
    public val whatsapp: WhatsappOTP
    public val email: EmailOTP

    public suspend fun authenticate(
        parameters: AuthParameters,
    ): BaseResponse

    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: BaseResponse) -> Unit,
    )

    public interface SmsOTP {

        public data class Parameters(
            val phoneNumber: String,
            val expirationMinutes: UInt = 60u,
        )

        public suspend fun loginOrCreate(parameters: Parameters): BaseResponse

        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )

    }

    public interface WhatsappOTP {

        public data class Parameters(
            val phoneNumber: String,
            val expirationMinutes: UInt = 60u,
        )

        public suspend fun loginOrCreate(parameters: Parameters): BaseResponse

        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )

    }

    public interface EmailOTP {

        public data class Parameters(
            val email: String,
            val expirationMinutes: UInt = 60u,
        )

        public suspend fun loginOrCreate(parameters: Parameters): BaseResponse

        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )

    }

}
