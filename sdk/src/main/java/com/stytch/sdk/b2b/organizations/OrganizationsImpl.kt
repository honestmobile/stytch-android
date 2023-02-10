package com.stytch.sdk.b2b.organizations

import com.stytch.sdk.b2b.OrganizationResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OrganizationsImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val api: StytchB2BApi.Organizations
) : Organizations {
    override suspend fun getOrganization(parameters: Organizations.GetOrganizationParameters): OrganizationResponse =
        withContext(dispatchers.io) {
            api.getOrganization(parameters.organizationId)
        }

    override fun getOrganization(
        parameters: Organizations.GetOrganizationParameters,
        callback: (OrganizationResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = getOrganization(parameters)
            callback(result)
        }
    }
}
