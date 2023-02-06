package com.stytch.sdk.otp

import com.stytch.sdk.AuthResponse
import com.stytch.sdk.BaseResponse
import com.stytch.sdk.LoginOrCreateOTPResponse
import com.stytch.sdk.StytchDispatchers
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.sessions.SessionStorage
import com.stytch.sdk.sessions.launchSessionUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OTPImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val api: StytchApi.OTP,
) : OTP {

    override val sms: OTP.SmsOTP = SmsOTPImpl()
    override val whatsapp: OTP.WhatsAppOTP = WhatsAppOTPImpl()
    override val email: OTP.EmailOTP = EmailOTPImpl()

    override suspend fun authenticate(parameters: OTP.AuthParameters): AuthResponse {
        val result: AuthResponse
        withContext(dispatchers.io) {
            // call backend endpoint
            result = api.authenticateWithOTP(
                token = parameters.token,
                methodId = parameters.methodId,
                sessionDurationMinutes = parameters.sessionDurationMinutes
            ).apply {
                launchSessionUpdater(dispatchers, sessionStorage)
            }
        }
        return result
    }

    override fun authenticate(
        parameters: OTP.AuthParameters,
        callback: (response: AuthResponse) -> Unit
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    private inner class SmsOTPImpl : OTP.SmsOTP {
        override suspend fun loginOrCreate(parameters: OTP.SmsOTP.Parameters): LoginOrCreateOTPResponse {
            val result: LoginOrCreateOTPResponse
            withContext(dispatchers.io) {
                result = api.loginOrCreateByOTPWithSMS(
                    phoneNumber = parameters.phoneNumber,
                    expirationMinutes = parameters.expirationMinutes
                )
            }

            return result
        }

        override fun loginOrCreate(
            parameters: OTP.SmsOTP.Parameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

        override suspend fun send(parameters: OTP.SmsOTP.Parameters): BaseResponse =
            withContext(dispatchers.io) {
                if (sessionStorage.activeSessionExists) {
                    api.sendOTPWithSMSSecondary(
                        phoneNumber = parameters.phoneNumber,
                        expirationMinutes = parameters.expirationMinutes,
                    )
                } else {
                    api.sendOTPWithSMSPrimary(
                        phoneNumber = parameters.phoneNumber,
                        expirationMinutes = parameters.expirationMinutes,
                    )
                }
            }

        override fun send(parameters: OTP.SmsOTP.Parameters, callback: (response: BaseResponse) -> Unit) {
            externalScope.launch(dispatchers.ui) {
                val result = send(parameters)
                callback(result)
            }
        }
    }

    private inner class WhatsAppOTPImpl : OTP.WhatsAppOTP {
        override suspend fun loginOrCreate(
            parameters: OTP.WhatsAppOTP.Parameters
        ): LoginOrCreateOTPResponse {
            val result: LoginOrCreateOTPResponse
            withContext(dispatchers.io) {
                result = api.loginOrCreateUserByOTPWithWhatsApp(
                    phoneNumber = parameters.phoneNumber,
                    expirationMinutes = parameters.expirationMinutes
                )
            }

            return result
        }

        override fun loginOrCreate(
            parameters: OTP.WhatsAppOTP.Parameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

        override suspend fun send(parameters: OTP.WhatsAppOTP.Parameters): BaseResponse =
            withContext(dispatchers.io) {
                if (sessionStorage.activeSessionExists) {
                    api.sendOTPWithWhatsAppSecondary(
                        phoneNumber = parameters.phoneNumber,
                        expirationMinutes = parameters.expirationMinutes,
                    )
                } else {
                    api.sendOTPWithWhatsAppPrimary(
                        phoneNumber = parameters.phoneNumber,
                        expirationMinutes = parameters.expirationMinutes,
                    )
                }
            }

        override fun send(parameters: OTP.WhatsAppOTP.Parameters, callback: (response: BaseResponse) -> Unit) {
            externalScope.launch(dispatchers.ui) {
                val result = send(parameters)
                callback(result)
            }
        }
    }

    private inner class EmailOTPImpl : OTP.EmailOTP {
        override suspend fun loginOrCreate(parameters: OTP.EmailOTP.Parameters): LoginOrCreateOTPResponse {
            val result: LoginOrCreateOTPResponse
            withContext(dispatchers.io) {
                result = api.loginOrCreateUserByOTPWithEmail(
                    email = parameters.email,
                    expirationMinutes = parameters.expirationMinutes,
                    loginTemplateId = parameters.loginTemplateId,
                    signupTemplateId = parameters.signupTemplateId,
                )
            }

            return result
        }

        override fun loginOrCreate(
            parameters: OTP.EmailOTP.Parameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

        override suspend fun send(parameters: OTP.EmailOTP.Parameters): BaseResponse = withContext(dispatchers.io) {
            if (sessionStorage.activeSessionExists) {
                api.sendOTPWithEmailSecondary(
                    email = parameters.email,
                    expirationMinutes = parameters.expirationMinutes,
                    loginTemplateId = parameters.loginTemplateId,
                    signupTemplateId = parameters.signupTemplateId,
                )
            } else {
                api.sendOTPWithEmailPrimary(
                    email = parameters.email,
                    expirationMinutes = parameters.expirationMinutes,
                    loginTemplateId = parameters.loginTemplateId,
                    signupTemplateId = parameters.signupTemplateId,
                )
            }
        }

        override fun send(parameters: OTP.EmailOTP.Parameters, callback: (response: BaseResponse) -> Unit) {
            externalScope.launch(dispatchers.ui) {
                val result = send(parameters)
                callback(result)
            }
        }
    }
}
