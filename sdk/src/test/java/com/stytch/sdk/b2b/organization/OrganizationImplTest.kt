package com.stytch.sdk.b2b.organization

import com.stytch.sdk.b2b.DeleteOrganizationResponse
import com.stytch.sdk.b2b.OrganizationResponse
import com.stytch.sdk.b2b.UpdateOrganizationResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.OrganizationData
import com.stytch.sdk.b2b.network.models.OrganizationDeleteResponseData
import com.stytch.sdk.b2b.network.models.OrganizationResponseData
import com.stytch.sdk.b2b.network.models.OrganizationUpdateResponseData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class OrganizationImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.Organization

    private lateinit var spiedSessionStorage: B2BSessionStorage

    private lateinit var impl: OrganizationImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulOrgResponse = StytchResult.Success<OrganizationResponseData>(mockk(relaxed = true))
    private val successfulDeleteResponse = StytchResult.Success<OrganizationDeleteResponseData>(mockk(relaxed = true))

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        every { StorageHelper.saveValue(any(), any()) } just runs
        spiedSessionStorage = spyk(B2BSessionStorage(StorageHelper), recordPrivateCalls = true)
        impl =
            OrganizationImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = spiedSessionStorage,
                api = mockApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `Organizations getOrganization delegates to api and caches the organization`() =
        runTest {
            coEvery { mockApi.getOrganization() } returns successfulOrgResponse
            val response = impl.get()
            assert(response is StytchResult.Success)
            coVerify { mockApi.getOrganization() }
            assert(spiedSessionStorage.organization == successfulOrgResponse.value.organization)
        }

    @Test
    fun `Organizations getOrganization with callback calls callback method`() {
        coEvery { mockApi.getOrganization() } returns successfulOrgResponse
        val mockCallback = spyk<(OrganizationResponse) -> Unit>()
        impl.get(mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `Organizations getSync delegates to sessionStorage`() {
        val mockOrganization: OrganizationData = mockk()
        every { spiedSessionStorage.organization } returns mockOrganization
        val member = impl.getSync()
        assert(member == mockOrganization)
        verify { spiedSessionStorage.organization }
    }

    @Test
    fun `Organizations update delegates to api and caches the updated organization`() =
        runTest {
            val mockResponse = StytchResult.Success<OrganizationUpdateResponseData>(mockk(relaxed = true))
            coEvery {
                mockApi.updateOrganization(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns mockResponse
            val response = impl.update(Organization.UpdateOrganizationParameters())
            assert(response is StytchResult.Success)
            coVerify { mockApi.updateOrganization() }
            assert(spiedSessionStorage.organization == mockResponse.value.organization)
        }

    @Test
    fun `Organizations update with callback calls callback method`() {
        coEvery {
            mockApi.updateOrganization(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns StytchResult.Success(mockk(relaxed = true))
        val mockCallback = spyk<(UpdateOrganizationResponse) -> Unit>()
        impl.update(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `Organizations delete delegates to api and clears all cached data`() =
        runTest {
            coEvery { mockApi.deleteOrganization() } returns successfulDeleteResponse
            val response = impl.delete()
            assert(response is StytchResult.Success)
            coVerify { mockApi.deleteOrganization() }
            assert(spiedSessionStorage.organization == null)
            assert(spiedSessionStorage.member == null)
            assert(spiedSessionStorage.memberSession == null)
            assert(spiedSessionStorage.sessionJwt == null)
            assert(spiedSessionStorage.sessionToken == null)
        }

    @Test
    fun `Organizations delete with callback calls callback method`() {
        coEvery { mockApi.deleteOrganization() } returns successfulDeleteResponse
        val mockCallback = spyk<(DeleteOrganizationResponse) -> Unit>()
        impl.delete(mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}
