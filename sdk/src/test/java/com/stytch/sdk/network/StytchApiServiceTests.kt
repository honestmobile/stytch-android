package com.stytch.sdk.network

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

private const val EMAIL = "email@email.com"
private const val LOGIN_MAGIC_LINK = "loginMagicLink://"

internal class StytchApiServiceTests {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: StytchApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(12345)

        apiService = StytchApiService.createApiService(mockWebServer.url("/").toString(), null)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // region MagicLinks

    @Test
    fun `check magic links email loginOrCreate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        runBlocking {
            try {
                apiService.loginOrCreateUserByEmail(
                    StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest(
                        EMAIL,
                        LOGIN_MAGIC_LINK,
                        "123",
                        "method2"
                    )
                ).data
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/magic_links/email/login_or_create")
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("login_magic_link_url\":\"${LOGIN_MAGIC_LINK}\""))
            assert(body.contains("code_challenge\":\"123\""))
            assert(body.contains("code_challenge_method\":\"method2\""))
        }
    }

    @Test
    fun `check magic links authenticate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.authenticate(
                    StytchRequests.MagicLinks.AuthenticateRequest(
                        "token",
                        "123",
                        60
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/magic_links/authenticate")
            assert(body.contains("token\":\"token\""))
            assert(body.contains("session_duration_minutes\":60"))
        }
    }

    // endregion MagicLinks

    // region OTP
    @Test
    fun `check OTP email loginOrCreate with default expiration request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.loginOrCreateUserByOTPWithEmail(
                    StytchRequests.OTP.Email(
                        EMAIL,
                        60
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/email/login_or_create")
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("expiration_minutes\":60"))
        }
    }

    @Test
    fun `check OTP sms loginOrCreate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.loginOrCreateUserByOTPWithSMS(
                    StytchRequests.OTP.SMS(
                        "000",
                        24
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/sms/login_or_create")
            assert(body.contains("phone_number\":\"000\""))
            assert(body.contains("expiration_minutes\":24"))
        }
    }

    @Test
    fun `check OTP whatsapp loginOrCreate with default expiration request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.loginOrCreateUserByOTPWithWhatsApp(
                    StytchRequests.OTP.WhatsApp(
                        "000",
                        60
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/whatsapp/login_or_create")
            assert(body.contains("phone_number\":\"000\""))
            assert(body.contains("expiration_minutes\":60"))
        }
    }

    @Test
    fun `check OTP authenticate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.authenticateWithOTP(
                    StytchRequests.OTP.Authenticate(
                        "token",
                        "methodId",
                        60
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/authenticate")
            assert(body.contains("token\":\"token\""))
            assert(body.contains("method_id\":\"methodId\""))
            assert(body.contains("session_duration_minutes\":60"))
        }
    }

    // endregion OTP

    // region Passwords

    @Test
    fun `check Passwords create request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.passwords(
                    StytchRequests.Passwords.CreateRequest(
                        EMAIL,
                        "123asd",
                        60
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/passwords")
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("password\":\"123asd\""))
            assert(body.contains("session_duration_minutes\":60"))
        }
    }

    @Test
    fun `check Passwords strenghtCheck request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.strengthCheck(
                    StytchRequests.Passwords.StrengthCheckRequest(
                        EMAIL,
                        "123asd"
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/passwords/strength_check")
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("password\":\"123asd\""))
        }
    }

    @Test
    fun `check Passwords resetbyEmail request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.resetByEmail(
                    StytchRequests.Passwords.ResetByEmailRequest(
                        "token",
                        "123asd",
                        60,
                        "ver1"
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/passwords/email/reset")
            assert(body.contains("token\":\"token\""))
            assert(body.contains("password\":\"123asd\""))
            assert(body.contains("session_duration_minutes\":60"))
            assert(body.contains("code_verifier\":\"ver1\""))
        }
    }

    @Test
    fun `check Passwords resetbyEmailStart request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.resetByEmailStart(
                    StytchRequests.Passwords.ResetByEmailStartRequest(
                        EMAIL,
                        "123",
                        "method2",
                        "loginRedirect",
                        24,
                        "resetPasswordUrl",
                        23
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/passwords/email/reset/start")
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("code_challenge\":\"123\""))
            assert(body.contains("code_challenge_method\":\"method2\""))
            assert(body.contains("login_redirect_url\":\"loginRedirect\""))
            assert(body.contains("reset_password_redirect_url\":\"resetPasswordUrl\""))
            assert(body.contains("login_expiration_minutes\":24"))
            assert(body.contains("reset_password_expiration_minutes\":23"))
        }
    }

    @Test
    fun `check Passwords authenticate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.authenticateWithPasswords(
                    StytchRequests.Passwords.AuthenticateRequest(
                        EMAIL,
                        "123asd",
                        46
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/passwords/authenticate")
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("password\":\"123asd\""))
            assert(body.contains("session_duration_minutes\":46"))
        }
    }

    // endregion Passwords

    // region Sessions

    @Test
    fun `check Sessions authenticate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.authenticateSessions(
                    StytchRequests.Sessions.AuthenticateRequest(
                        24
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            val body = request.body.readUtf8()
            assert(request.method == "POST")
            assert(request.path == "/sessions/authenticate")
            assert(body.contains("session_duration_minutes\":24"))
        }
    }

    @Test
    fun `check Sessions revoke request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.revokeSessions()
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "POST")
            assert(request.path == "/sessions/revoke")
        }
    }

    // endregion Sessions

    // region Biometrics
    @Test
    fun `check biometricsRegisterStart request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.biometricsRegisterStart(
                    StytchRequests.Biometrics.RegisterStartRequest(publicKey = "publicKey")
                )
            } catch (_: Exception) {}
            val request = mockWebServer.takeRequest()
            val body = request.body.readUtf8()
            assert(request.method == "POST")
            assert(request.path == "/biometrics/register/start")
            assert(body.contains("public_key\":\"publicKey\""))
        }
    }

    @Test
    fun `check biometricsRegister request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.biometricsRegister(
                    StytchRequests.Biometrics.RegisterRequest(
                        signature = "signature",
                        biometricRegistrationId = "biometricRegistrationId",
                        sessionDurationMinutes = 30
                    )
                )
            } catch (_: Exception) {}
            val request = mockWebServer.takeRequest()
            val body = request.body.readUtf8()
            assert(request.method == "POST")
            assert(request.path == "/biometrics/register")
            assert(body.contains("signature\":\"signature\""))
            assert(body.contains("biometric_registration_id\":\"biometricRegistrationId\""))
            assert(body.contains("session_duration_minutes\":30"))
        }
    }

    @Test
    fun `check biometricsAuthenticateStart request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.biometricsAuthenticateStart(
                    StytchRequests.Biometrics.AuthenticateStartRequest(publicKey = "publicKey")
                )
            } catch (_: Exception) {}
            val request = mockWebServer.takeRequest()
            val body = request.body.readUtf8()
            assert(request.method == "POST")
            assert(request.path == "/biometrics/authenticate/start")
            assert(body.contains("public_key\":\"publicKey\""))
        }
    }

    @Test
    fun `check biometricsAuthenticate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.biometricsAuthenticate(
                    StytchRequests.Biometrics.AuthenticateRequest(
                        signature = "signature",
                        biometricRegistrationId = "biometricRegistrationId",
                        sessionDurationMinutes = 30
                    )
                )
            } catch (_: Exception) {}
            val request = mockWebServer.takeRequest()
            val body = request.body.readUtf8()
            assert(request.method == "POST")
            assert(request.path == "/biometrics/authenticate")
            assert(body.contains("signature\":\"signature\""))
            assert(body.contains("biometric_registration_id\":\"biometricRegistrationId\""))
            assert(body.contains("session_duration_minutes\":30"))
        }
    }
    // endregion Biometrics

    // region UserManagement
    @Test
    fun `check getUser request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.getUser()
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "GET")
            assert(request.path == "/users/me")
        }
    }

    @Test
    fun `check deleteEmailById request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.deleteEmailById("email_id")
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "DELETE")
            assert(request.path == "/users/emails/email_id")
        }
    }

    @Test
    fun `check deletePhoneNumberById request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.deletePhoneNumberById("phone_number_id")
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "DELETE")
            assert(request.path == "/users/phone_numbers/phone_number_id")
        }
    }

    @Test
    fun `check deleteBiometricRegistrationById request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.deleteBiometricRegistrationById("biometrics_registration_id")
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "DELETE")
            assert(request.path == "/users/biometric_registrations/biometrics_registration_id")
        }
    }

    @Test
    fun `check deleteCryptoWalletById request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.deleteCryptoWalletById("crypto_wallet_id")
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "DELETE")
            assert(request.path == "/users/crypto_wallets/crypto_wallet_id")
        }
    }

    @Test
    fun `check deleteWebAuthnById request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.deleteWebAuthnById("webauthn_registration_id")
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            assert(request.method == "DELETE")
            assert(request.path == "/users/webauthn_registrations/webauthn_registration_id")
        }
    }
    // endregion UserManagement

    // region OAuth
    @Test
    fun `check authenticateWithGoogleIdToken request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.authenticateWithGoogleIdToken(
                    StytchRequests.OAuth.Google.AuthenticateRequest(
                        idToken = "id_token",
                        nonce = "nonce",
                        sessionDurationMinutes = 30
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            val body = request.body.readUtf8()
            assert(request.method == "POST")
            assert(request.path == "/oauth/google/id_token/authenticate")
            assert(body.contains("id_token\":\"id_token\""))
            assert(body.contains("nonce\":\"nonce\""))
            assert(body.contains("session_duration_minutes\":30"))
        }
    }

    @Test
    fun `check authenticateWithThirdPartyToken request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            try {
                apiService.authenticateWithThirdPartyToken(
                    StytchRequests.OAuth.ThirdParty.AuthenticateRequest(
                        token = "id_token",
                        sessionDurationMinutes = 30,
                        codeVerifier = "code_challenge"
                    )
                )
            } catch (_: Exception) {
            }
            val request = mockWebServer.takeRequest()
            val body = request.body.readUtf8()
            assert(request.method == "POST")
            assert(request.path == "/oauth/authenticate")
            assert(body.contains("token\":\"id_token\""))
            assert(body.contains("session_duration_minutes\":30"))
            assert(body.contains("code_verifier\":\"code_challenge\""))
        }
    }
    // endregion OAuth
}
