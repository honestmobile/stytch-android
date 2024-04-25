package com.stytch.sdk.consumer.crypto

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.CryptoWalletAuthenticateStartResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class CryptoWalletImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.Crypto,
) : CryptoWallet {
    override suspend fun authenticateStart(
        parameters: CryptoWallet.AuthenticateStartParameters,
    ): CryptoWalletAuthenticateStartResponse {
        return withContext(dispatchers.io) {
            if (sessionStorage.persistedSessionIdentifiersExist) {
                api.authenticateStartSecondary(
                    cryptoWalletAddress = parameters.cryptoWalletAddress,
                    cryptoWalletType = parameters.cryptoWalletType,
                )
            } else {
                api.authenticateStartPrimary(
                    cryptoWalletAddress = parameters.cryptoWalletAddress,
                    cryptoWalletType = parameters.cryptoWalletType,
                )
            }
        }
    }

    override fun authenticateStart(
        parameters: CryptoWallet.AuthenticateStartParameters,
        callback: (CryptoWalletAuthenticateStartResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(authenticateStart(parameters))
        }
    }

    override suspend fun authenticate(parameters: CryptoWallet.AuthenticateParameters): AuthResponse {
        return withContext(dispatchers.io) {
            api.authenticate(
                cryptoWalletAddress = parameters.cryptoWalletAddress,
                cryptoWalletType = parameters.cryptoWalletType,
                signature = parameters.signature,
                sessionDurationMinutes = parameters.sessionDurationMinutes,
            ).apply {
                launchSessionUpdater(dispatchers, sessionStorage)
            }
        }
    }

    override fun authenticate(
        parameters: CryptoWallet.AuthenticateParameters,
        callback: (AuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(authenticate(parameters))
        }
    }
}