package com.stytch.sessions

import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.responseData.AuthData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.random.Random

private const val SECOND = 1000L
private const val MINUTE = 60L * SECOND

private const val DEFAULT_DELAY = 3 * MINUTE
private const val MAXIMUM_DELAY = 5 * MINUTE

private const val MAXIMUM_RANDOM_MILLIS = SECOND
private const val MAXIMUM_BACKOFF_DELAY = 32 * SECOND

internal object SessionAutoUpdater {
    private var sessionUpdateJob: Job? = null
    private var n = 0
    private var sessionUpdateDelay: Long = DEFAULT_DELAY
    private var backoffStartMillis: Long = 0

    fun startSessionUpdateJob() {
//        prevent multiple update jobs running
        stopSessionUpdateJob()
        sessionUpdateJob = GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                // wait before another update request
                delay(sessionUpdateDelay)
                // request session update from backend
                val sessionResult = updateSession()
                //save session data in SessionStorage if call successful
                if (sessionResult is StytchResult.Success) {
                    // reset exponential backoff delay
                    resetDelay()
                    // save session
                    sessionResult.saveSession()
                } else {
                    // set backoff start if not set
                    if (backoffStartMillis <= 0) {
                        backoffStartMillis = System.currentTimeMillis()
                    }
                    // if delay reached max delay stop exponential backoff
                    if (System.currentTimeMillis() - backoffStartMillis > MAXIMUM_DELAY - DEFAULT_DELAY) {
                        resetDelay()
                        // stop auto updater/ exit while loop
                        break
                    } else {
                        // set exponential delay
                        sessionUpdateDelay = minOf(
                            (2.0.pow(n) + Random.nextLong(0, MAXIMUM_RANDOM_MILLIS)).toLong(),
                            MAXIMUM_BACKOFF_DELAY
                        )
                        n++
                    }

                }
            }
        }
    }

    private fun resetDelay() {
        n = 0
        sessionUpdateDelay = DEFAULT_DELAY
        backoffStartMillis = 0
    }

    fun stopSessionUpdateJob() {
        sessionUpdateJob?.cancel()
        sessionUpdateJob = null
    }

    private suspend fun updateSession(): StytchResult<AuthData> {
        val result: StytchResult<AuthData>
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.Sessions.authenticate(
                null
            )
        }
        return result
    }
}

/**
 * Starts session update in background
 */
internal fun StytchResult<AuthData>.launchSessionUpdater() {
    if (this is StytchResult.Success) {
//        save session data
        saveSession()
//        start auto session update
        SessionAutoUpdater.startSessionUpdateJob()
    }
}