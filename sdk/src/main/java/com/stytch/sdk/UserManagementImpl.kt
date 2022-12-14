package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.responseData.UserData
import com.stytch.sessions.SessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class UserManagementImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val api: StytchApi.UserManagement,
) : UserManagement {
    override suspend fun getUser(): UserResponse = withContext(dispatchers.io) {
        val user = api.getUser()
        when (user) {
            is StytchResult.Success -> sessionStorage.user = user.value
            is StytchResult.Error -> StytchLog.e(user.exception.reason?.toString() ?: "Error updating cached user")
        }
        user
    }

    override fun getUser(callback: (UserResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = getUser()
            callback(result)
        }
    }

    override fun getSyncUser(): UserData? = sessionStorage.user

    override suspend fun deleteFactor(factor: AuthenticationFactor): BaseResponse {
        val result = withContext(dispatchers.io) {
            when (factor) {
                is AuthenticationFactor.Email -> api.deleteEmailById(factor.id)
                is AuthenticationFactor.PhoneNumber -> api.deletePhoneNumberById(factor.id)
                is AuthenticationFactor.BiometricRegistration -> api.deleteBiometricRegistrationById(factor.id)
                is AuthenticationFactor.CryptoWallet -> api.deleteCryptoWalletById(factor.id)
                is AuthenticationFactor.WebAuthn -> api.deleteWebAuthnById(factor.id)
            }
        }
        getUser()
        return result
    }

    override fun deleteFactor(factor: AuthenticationFactor, callback: (BaseResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = deleteFactor(factor)
            callback(result)
        }
    }
}
