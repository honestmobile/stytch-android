package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OTPImpl internal constructor() : OTP {

    override val sms: OTP.SmsOTP = SmsOTPImpl()
    override val whatsapp: OTP.WhatsAppOTP = WhatsAppOTPImpl()
    override val email: OTP.EmailOTP = EmailOTPImpl()

    override suspend fun authenticate(parameters: OTP.AuthParameters): BaseResponse {
        val result: BaseResponse
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.OTP.authenticateWithOTP(
                token = parameters.token,
                sessionDurationMinutes = parameters.sessionDurationMinutes
            )
        }
        return result
    }

    override fun authenticate(parameters: OTP.AuthParameters, callback: (response: BaseResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    private inner class SmsOTPImpl : OTP.SmsOTP {
        override suspend fun loginOrCreate(parameters: OTP.SmsOTP.Parameters): BaseResponse {
            val result: BaseResponse
            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.OTP.loginOrCreateByOTPWithSMS(
                    phoneNumber = parameters.phoneNumber,
                    expirationMinutes = parameters.expirationMinutes
                )
            }

            return result
        }

        override fun loginOrCreate(parameters: OTP.SmsOTP.Parameters, callback: (response: BaseResponse) -> Unit) {
            GlobalScope.launch(StytchClient.uiDispatcher) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

    }

    private inner class WhatsAppOTPImpl : OTP.WhatsAppOTP {
        override suspend fun loginOrCreate(parameters: OTP.WhatsAppOTP.Parameters): BaseResponse {
            val result: BaseResponse
            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.OTP.loginOrCreateUserByOTPWithWhatsApp(
                    phoneNumber = parameters.phoneNumber,
                    expirationMinutes = parameters.expirationMinutes
                )
            }

            return result
        }

        override fun loginOrCreate(parameters: OTP.WhatsAppOTP.Parameters, callback: (response: BaseResponse) -> Unit) {
            GlobalScope.launch(StytchClient.uiDispatcher) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

    }

    private inner class EmailOTPImpl : OTP.EmailOTP {
        override suspend fun loginOrCreate(parameters: OTP.EmailOTP.Parameters): BaseResponse {
            val result: BaseResponse
            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.OTP.loginOrCreateUserByOTPWithEmail(
                    email = parameters.email,
                    expirationMinutes = parameters.expirationMinutes
                )
            }

            return result
        }

        override fun loginOrCreate(parameters: OTP.EmailOTP.Parameters, callback: (response: BaseResponse) -> Unit) {
            GlobalScope.launch(StytchClient.uiDispatcher) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

    }
}
