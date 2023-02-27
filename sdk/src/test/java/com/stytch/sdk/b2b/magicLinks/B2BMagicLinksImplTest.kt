package com.stytch.sdk.b2b.magicLinks

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.B2BEMLAuthenticateData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sessions.SessionAutoUpdater
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
internal class B2BMagicLinksImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.MagicLinks.Email

    @MockK
    private lateinit var mockB2BSessionStorage: B2BSessionStorage

    @MockK
    private lateinit var mockStorageHelper: StorageHelper

    private lateinit var impl: B2BMagicLinksImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulAuthResponse = StytchResult.Success<B2BEMLAuthenticateData>(mockk(relaxed = true))
    private val authParameters = mockk<B2BMagicLinks.AuthParameters>(relaxed = true)
    private val emailMagicLinkParameters = mockk<B2BMagicLinks.EmailMagicLinks.Parameters>(relaxed = true)
    private val successfulLoginOrCreateResponse = mockk<BaseResponse>()

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.b2b.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        impl = B2BMagicLinksImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            sessionStorage = mockB2BSessionStorage,
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
    fun `MagicLinksImpl authenticate returns error if codeverifier fails`() = runTest {
        every { mockStorageHelper.loadValue(any()) } returns null
        val response = impl.authenticate(authParameters)
        assert(response is StytchResult.Error)
    }

    @Test
    fun `MagicLinksImpl authenticate delegates to api`() = runTest {
        every { mockStorageHelper.retrieveCodeVerifier() } returns ""
        coEvery { mockApi.authenticate(any(), any(), any()) } returns successfulAuthResponse
        val response = impl.authenticate(authParameters)
        assert(response is StytchResult.Success)
        coVerify { mockApi.authenticate(any(), any(), any()) }
        verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `MagicLinksImpl authenticate with callback calls callback method`() {
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        impl.authenticate(authParameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `MagicLinksImpl email loginOrCreate returns error if generateCodeChallenge fails`() = runTest {
        every { mockStorageHelper.generateHashedCodeChallenge() } throws RuntimeException("Test")
        val response = impl.email.loginOrSignup(emailMagicLinkParameters)
        assert(response is StytchResult.Error)
    }

    @Test
    fun `MagicLinksImpl email loginOrCreate delegates to api`() = runTest {
        every { mockStorageHelper.generateHashedCodeChallenge() } returns Pair("", "")
        coEvery {
            mockApi.loginOrSignupByEmail(any(), any(), any(), any(), any(), any(), any())
        } returns successfulLoginOrCreateResponse
        impl.email.loginOrSignup(emailMagicLinkParameters)
        coVerify { mockApi.loginOrSignupByEmail(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `MagicLinksImpl email loginOrCreate with callback calls callback method`() {
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.email.loginOrSignup(emailMagicLinkParameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}
