package com.stytch.exampleapp.b2b

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.oauth.OAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OAuthViewModel : ViewModel() {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun startGoogleOAuthFlow(context: Activity) {
        viewModelScope.launchAndToggleLoadingState {
            StytchB2BClient.oauth.google.start(
                OAuth.Provider.StartParameters(
                    context = context,
                    oAuthRequestIdentifier = B2B_OAUTH_REQUEST,
                    organizationId = BuildConfig.STYTCH_B2B_ORG_ID,
                    loginRedirectUrl = "app://b2bexampleapp.com/",
                    signupRedirectUrl = "app://b2bexampleapp.com/",
                ),
            )
        }
    }

    fun startGoogleDiscoveryOAuthFlow(context: Activity) {
        viewModelScope.launchAndToggleLoadingState {
            StytchB2BClient.oauth.google.discovery.start(
                OAuth.ProviderDiscovery.DiscoveryStartParameters(
                    context = context,
                    oAuthRequestIdentifier = B2B_OAUTH_REQUEST,
                    discoveryRedirectUrl = "app://b2bexampleapp.com/",
                ),
            )
        }
    }

    private fun CoroutineScope.launchAndToggleLoadingState(block: suspend () -> Unit): DisposableHandle =
        launch {
            _loadingState.value = true
            block()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
}
