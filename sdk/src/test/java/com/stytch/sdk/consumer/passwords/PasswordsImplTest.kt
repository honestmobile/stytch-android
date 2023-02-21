package com.stytch.sdk.consumer.passwords

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.BasicData
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.PasswordsCreateResponse
import com.stytch.sdk.consumer.PasswordsStrengthCheckResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.AuthData
import com.stytch.sdk.consumer.network.CreateResponse
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import java.security.KeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class PasswordsImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.Passwords

    @MockK
    private lateinit var mockSessionStorage: ConsumerSessionStorage

    @MockK
    private lateinit var mockStorageHelper: StorageHelper

    private lateinit var impl: PasswordsImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulAuthResponse = StytchResult.Success<AuthData>(mockk(relaxed = true))
    private val authParameters = mockk<Passwords.AuthParameters>(relaxed = true)
    private val createParameters = mockk<Passwords.CreateParameters>(relaxed = true)
    private val successfulCreateResponse = StytchResult.Success<CreateResponse>(mockk(relaxed = true))
    private val resetByEmailStartParameters = Passwords.ResetByEmailStartParameters(
        email = "emailAddress",
        loginRedirectUrl = null,
        loginExpirationMinutes = null,
        resetPasswordRedirectUrl = null,
        resetPasswordExpirationMinutes = null,
        resetPasswordTemplateId = null,
    )
    private val resetByEmailParameters = mockk<Passwords.ResetByEmailParameters>(relaxed = true)
    private val strengthCheckParameters = mockk<Passwords.StrengthCheckParameters>(relaxed = true)

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.consumer.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        MockKAnnotations.init(this, true, true)
        impl = PasswordsImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            sessionStorage = mockSessionStorage,
            storageHelper = mockStorageHelper,
            api = mockApi
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `PasswordsImpl authenticate delegates to api`() = runTest {
        coEvery { mockApi.authenticate(any(), any(), any()) } returns successfulAuthResponse
        val response = impl.authenticate(authParameters)
        assert(response is StytchResult.Success)
        coVerify { mockApi.authenticate(any(), any(), any()) }
        verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `PasswordsImpl authenticate with callback calls callback method`() {
        coEvery { mockApi.authenticate(any(), any(), any()) } returns successfulAuthResponse
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        impl.authenticate(authParameters, mockCallback)
        verify { mockCallback.invoke(eq(successfulAuthResponse)) }
    }

    @Test
    fun `PasswordsImpl create delegates to api`() = runTest {
        coEvery { mockApi.create(any(), any(), any()) } returns successfulCreateResponse
        val response = impl.create(createParameters)
        assert(response is StytchResult.Success)
        coVerify { mockApi.create(any(), any(), any()) }
        verify { successfulCreateResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `PasswordsImpl create with callback calls callback method`() {
        coEvery { mockApi.create(any(), any(), any()) } returns successfulCreateResponse
        val mockCallback = spyk<(PasswordsCreateResponse) -> Unit>()
        impl.create(createParameters, mockCallback)
        verify { mockCallback.invoke(eq(successfulCreateResponse)) }
    }

    @Test
    fun `PasswordsImpl resetByEmailStart returns error if generateHashedCodeChallenge fails`() = runTest {
        every { mockStorageHelper.generateHashedCodeChallenge() } throws RuntimeException("Test")
        val response = impl.resetByEmailStart(resetByEmailStartParameters)
        assert(response is StytchResult.Error)
    }

    @Test
    fun `PasswordsImpl resetByEmailStart delegates to api`() = runTest {
        every { mockStorageHelper.generateHashedCodeChallenge() } returns Pair("", "")
        coEvery { mockApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any(), any()) } returns mockk()
        impl.resetByEmailStart(resetByEmailStartParameters)
        coVerify { mockApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `PasswordsImpl resetByEmailStart with callback calls callback method`() {
        val mockResponse: StytchResult<BasicData> = mockk()
        coEvery {
            mockApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any(), any())
        } returns mockResponse
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.resetByEmailStart(resetByEmailStartParameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `PasswordsImpl resetByEmail returns error if codeVerifier fails`() = runTest {
        every { mockStorageHelper.loadValue(any()) } returns null
        val response = impl.resetByEmail(resetByEmailParameters)
        assert(response is StytchResult.Error)
    }

    @Test
    fun `PasswordsImpl resetByEmail delegates to api`() = runTest {
        every { mockStorageHelper.retrieveCodeVerifier() } returns ""
        coEvery { mockApi.resetByEmail(any(), any(), any(), any()) } returns successfulAuthResponse
        impl.resetByEmail(resetByEmailParameters)
        coVerify { mockApi.resetByEmail(any(), any(), any(), any()) }
        verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `PasswordsImpl resetByEmail with callback calls callback method`() {
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        impl.resetByEmail(resetByEmailParameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `PasswordsImpl strengthCheck delegates to api`() = runTest {
        coEvery { mockApi.strengthCheck(any(), any()) } returns mockk()
        impl.strengthCheck(strengthCheckParameters)
        coVerify { mockApi.strengthCheck(any(), any()) }
    }

    @Test
    fun `PasswordsImpl strengthCheck with callback calls callback method`() {
        coEvery { mockApi.strengthCheck(any(), any()) } returns mockk()
        val mockCallback = spyk<(PasswordsStrengthCheckResponse) -> Unit>()
        impl.strengthCheck(strengthCheckParameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}
