package com.stytch.sdk

import android.content.Context
import android.net.Uri
import android.os.Build
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchResponses
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

public typealias LoginOrCreateUserByEmailResponse = StytchResult<StytchResponses.LoginOrCreateUserByEmailResponse>
public typealias BaseResponse = StytchResult<StytchResponses.BasicResponse>

/**
 * The entrypoint for all Stytch-related interaction.
 */
public object StytchClient {

    private var ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private var uiDispatcher: CoroutineDispatcher = Dispatchers.Main

    /**
     * Configures the StytchClient, setting the publicToken and hostUrl.
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @param hostUrl This is an https url which will be used as the domain for setting session-token cookies to be sent to your servers on subsequent requests
     */
    public fun configure(context: Context, publicToken: String, hostUrl: String) {
        val deviceInfo = getDeviceInfo(context)
        StytchApi.configure(publicToken, hostUrl, deviceInfo)
    }

    internal fun assertInitialized() {
        if (!StytchApi.isInitialized) {
            stytchError("StytchApi not configured. You must call 'StytchApi.configure(...)' before using any functionality of the StytchApi.")
        }
    }

    /**
     * Set dispatchers for UI and IO tasks
     */
    public fun setDispatchers(uiDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) {
        this.uiDispatcher = uiDispatcher
        this.ioDispatcher = ioDispatcher
    }

    public object MagicLinks {

        public data class Parameters(
            val email: String,
            val loginMagicLinkUrl: String? = null,
            val loginExpirationInMinutes: Int? = null,
            val signupMagicLinkUrl: String? = null,
            val signupExpirationInMinutes: Int? = null,
        )

        /**
         * Wraps Stytch’s email magic link login_or_create endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive magic link
         * @return LoginOrCreateUserByEmailResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: Parameters): LoginOrCreateUserByEmailResponse {
            assertInitialized()
            return StytchApi.MagicLinks.Email.loginOrCreateEmail(email = parameters.email,
                loginMagicLinkUrl = parameters.loginMagicLinkUrl,
                signupMagicLinkUrl = parameters.signupMagicLinkUrl,
                loginExpirationMinutes = parameters.loginExpirationInMinutes,
                signupExpirationMinutes = parameters.signupExpirationInMinutes)
        }

        /**
         * Wraps Stytch’s email magic link login_or_create endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive magic link
         * @param callback calls callback with LoginOrCreateUserByEmailResponse response from backend
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: LoginOrCreateUserByEmailResponse) -> Unit,
        ) {
//          call endpoint in IO thread
            GlobalScope.launch(ioDispatcher) {
                val result = loginOrCreate(parameters)
//              change to main thread to call callback
                withContext(uiDispatcher) {
                    callback(result)
                }
            }
        }

        /**
         * Wraps the magic link authenticate API endpoint which validates the magic link token passed in. If this method succeeds, the user will be logged in, granted an active session
         * @param parameters required to receive magic link
         * @return LoginOrCreateUserByEmailResponse response from backend
         */
        public suspend fun authenticate(token: String, sessionExpirationMinutes: Int = 60): BaseResponse {
            assertInitialized()
            return StytchApi.MagicLinks.Email.authenticate(token, sessionExpirationMinutes)
        }

        public fun authenticate(
            token: String,
            sessionExpirationMinutes: Int = 60,
            callback: (response: BaseResponse) -> Unit,
        ) {
//          call endpoint in IO thread
            GlobalScope.launch(ioDispatcher) {
                val result = authenticate(token, sessionExpirationMinutes)
//              change to main thread to call callback
                withContext(uiDispatcher) {
                    callback(result)
                }
            }
        }

    }

    //    TODO:("Sessions")
    public object Sessions {
//    fun revoke(completion:)
//    fun authenticate(parameters:completion:)
    }

    //    TODO:("OTP")
    public object OneTimePasscodes {
//    fun loginOrCreate(parameters:completion:)
//    fun authenticate(parameters:completion:)
    }

//    TODO("OAuth")
//    TODO("User Management")

    private fun getDeviceInfo(context: Context): DeviceInfo {
        val deviceInfo = DeviceInfo()
        deviceInfo.applicationPackageName = context.applicationContext.packageName
        deviceInfo.osVersion = Build.VERSION.SDK_INT.toString()
        deviceInfo.deviceName = Build.MODEL
        deviceInfo.osName = Build.VERSION.CODENAME

        try {
//          throw exceptions if packageName not found
            deviceInfo.applicationVersion =
                context.applicationContext.packageManager.getPackageInfo(deviceInfo.applicationPackageName!!, 0).versionName
        } catch (ex: Exception) {
            deviceInfo.applicationVersion = ""
        }

        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels

        deviceInfo.screenSize = "($width,$height)"
        return deviceInfo
    }

    /**
     * Handle magic link
     * @param uri - intent.data from deep link
     * @param sessionDuration - sessionDuration
     */
    public suspend fun handle(uri: Uri, sessionDuration: Int): BaseResponse {
        val token = uri.getQueryParameter(Constants.QUERY_TOKEN)
        val tokenType = TokenType.fromString(uri.getQueryParameter(Constants.QUERY_TOKEN_TYPE))
        val publicToken = uri.getQueryParameter(Constants.QUERY_PUBLIC_TOKEN)

        if (token.isNullOrEmpty() || publicToken.isNullOrEmpty())
             TODO("create a more graceful handling of bad parameters")

        when (tokenType) {
            TokenType.MAGIC_LINKS -> {
                return MagicLinks.authenticate(token = token, sessionExpirationMinutes = sessionDuration)
            }
            TokenType.OAUTH ->{
                TODO("Implement oauth handling")
            }
            TokenType.PASSWORD_RESET ->{
                TODO("Implement password reset handling")
            }
            TokenType.UNKNOWN -> {
                TODO("return Error")
            }
        }
    }
}
