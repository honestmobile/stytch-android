package com.stytch.sdk

import android.content.Context
import com.stytch.sdk.network.StytchApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import java.security.KeyStore

internal class StytchClientTest {

    var mContextMock = mockk<Context>(relaxed = true)
    val mainThreadSurrogate = newSingleThreadContext("UI thread")
    val dispatcher = Dispatchers.Unconfined

    val magicLinkParams = MagicLinks.EmailMagicLinks.Parameters(
        email = "email@email.com"
    )

    val otpEmailParams = OTP.EmailParameters(
        email = "email@email.com",
        expirationInMinutes = 60u
    )

    val otpPhoneParams = OTP.PhoneParameters(
        phoneNumber = "+1200000000",
        expirationInMinutes = 60u
    )

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling EmailMagicLinks_loginOrCreate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.magicLinks.email.loginOrCreate(magicLinkParams)
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling OTP_loginOrCreateUserWithSMS`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.otp.loginOrCreateUserWithSMS(otpPhoneParams)
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling OTP_loginOrCreateUserWithWhatsapp`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.otp.loginOrCreateUserWithWhatsapp(otpPhoneParams)
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling OTP_loginOrCreateUserWithEmail`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.otp.loginOrCreateUserWithEmail(otpEmailParams)
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling EmailMagicLinks_authenticate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token"))
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling OTP_authenticate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.otp.authenticate(OTP.AuthParameters(token = "token"))
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `should trigger StytchApi configure when calling StytchClient configure`() {
        mockkObject(StytchApi)
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        val deviceInfo = DeviceInfo()
        every { stytchClientObject["getDeviceInfo"].invoke(mContextMock) }.returns(deviceInfo)
        stytchClientObject.configure(mContextMock, "", "")
        verify { StytchApi.configure("", "", deviceInfo) }
    }

    @Before
    fun before() {
        mockkConstructor(StorageHelper::class)
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any(), any()) } returns Unit
        mContextMock = mockk<Context>(relaxed = true)
        Dispatchers.setMain(mainThreadSurrogate)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        every { anyConstructed<StorageHelper>().loadValue(any()) } returns ""
        every { anyConstructed<StorageHelper>().getHashedCodeChallenge(any()) } returns Pair("", "")
    }

    @After
    fun after() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        unmockkAll()
    }

    @Test
    fun `should return result success loginOrCreate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreateEmail(
                email = magicLinkParams.email,
                codeChallenge = "",
                codeChallengeMethod = "",
                loginMagicLinkUrl = null
            )
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.magicLinks.email.loginOrCreate(magicLinkParams)
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success loginOrCreate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreateEmail(
                email = magicLinkParams.email,
                loginMagicLinkUrl = null,
                codeChallenge = any(),
                codeChallengeMethod = any())
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.magicLinks.email.loginOrCreate(magicLinkParams) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success authenticate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate(any(), codeVerifier = any(), sessionDurationMinutes = any())
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token"))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success authenticate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate("token", sessionDurationMinutes = 60u, codeVerifier = "")
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token")) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success OTP_authenticate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.authenticateWithOTP("token", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.otp.authenticate(OTP.AuthParameters("token", 60u)) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success OTP_authenticate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.authenticateWithOTP("token", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.otp.authenticate(OTP.AuthParameters("token", 60u))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success OTP_loginOrCreateUserWithSMS called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateByOTPWithSMS("+12000000", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.otp.loginOrCreateUserWithSMS(OTP.PhoneParameters("+12000000", 60u)) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success OTP_loginOrCreateUserWithSMS called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateByOTPWithSMS("+12000000", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.otp.loginOrCreateUserWithSMS(OTP.PhoneParameters("+12000000", 60u))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success OTP_loginOrCreateUserWithWhatsapp called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithWhatsapp("+12000000", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.otp.loginOrCreateUserWithWhatsapp(OTP.PhoneParameters("+12000000", 60u)) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success OTP_loginOrCreateUserWithWhatsapp called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithWhatsapp("+12000000", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.otp.loginOrCreateUserWithWhatsapp(OTP.PhoneParameters("+12000000", 60u))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success OTP_loginOrCreateUserWithEmail called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithEmail("+12000000", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.otp.loginOrCreateUserWithEmail(OTP.EmailParameters("email@email.com", 60u)) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success OTP_loginOrCreateUserWithEmail called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithEmail("+12000000", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.otp.loginOrCreateUserWithEmail(OTP.EmailParameters("email@email.com", 60u))
        }
        assert(result is StytchResult.Success)
    }
}
