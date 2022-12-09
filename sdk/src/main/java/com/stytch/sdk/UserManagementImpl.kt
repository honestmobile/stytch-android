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
        api.getUser()
    }

    override fun getUser(callback: (UserResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = getUser()
            callback(result)
        }
    }

    override fun getSyncUser(): UserData? = sessionStorage.user

    override suspend fun deleteFactor(factor: UserAuthenticationFactor): BaseResponse = withContext(dispatchers.io) {
        when (factor) {
            is UserAuthenticationFactor.Email -> api.deleteEmailById(factor.id)
            is UserAuthenticationFactor.PhoneNumber -> api.deletePhoneNumberById(factor.id)
            is UserAuthenticationFactor.BiometricRegistration -> api.deleteBiometricRegistrationById(factor.id)
            is UserAuthenticationFactor.CryptoWallet -> api.deleteCryptoWalletById(factor.id)
            is UserAuthenticationFactor.WebAuthn -> api.deleteWebAuthnById(factor.id)
        }
    }

    override fun deleteFactor(factor: UserAuthenticationFactor, callback: (BaseResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = deleteFactor(factor)
            callback(result)
        }
    }
}
