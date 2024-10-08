package com.stytch.sdk.b2b.searchManager

import com.stytch.sdk.b2b.B2BSearchMemberResponse
import com.stytch.sdk.b2b.B2BSearchOrganizationResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class SearchManagerImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val api: StytchB2BApi.SearchManager,
) : SearchManager {
    override suspend fun searchOrganization(
        parameters: SearchManager.SearchOrganizationParameters,
    ): B2BSearchOrganizationResponse =
        withContext(dispatchers.io) {
            api.searchOrganizations(
                organizationSlug = parameters.organizationSlug,
            )
        }

    override fun searchOrganization(
        parameters: SearchManager.SearchOrganizationParameters,
        callback: (B2BSearchOrganizationResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(searchOrganization(parameters))
        }
    }

    override fun searchOrganizationCompletable(
        parameters: SearchManager.SearchOrganizationParameters,
    ): CompletableFuture<B2BSearchOrganizationResponse> =
        externalScope
            .async {
                searchOrganization(parameters)
            }.asCompletableFuture()

    override suspend fun searchMember(parameters: SearchManager.SearchMemberParameters): B2BSearchMemberResponse =
        withContext(dispatchers.io) {
            api.searchMembers(
                emailAddress = parameters.emailAddress,
                organizationId = parameters.organizationId,
            )
        }

    override fun searchMember(
        parameters: SearchManager.SearchMemberParameters,
        callback: (B2BSearchMemberResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(searchMember(parameters))
        }
    }

    override fun searchMemberCompletable(
        parameters: SearchManager.SearchMemberParameters,
    ): CompletableFuture<B2BSearchMemberResponse> =
        externalScope
            .async {
                searchMember(parameters)
            }.asCompletableFuture()
}
