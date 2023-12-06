package com.stytch.sdk.common

import com.stytch.sdk.common.errors.StytchError

/**
 * Provides a wrapper for responses from the Stytch API
 */
public sealed class StytchResult<out T> {
    /**
     * Data class that can hold a successful response from a Stytch endpoint
     * @property value is the value of the response
     */
    public data class Success<out T>(val value: T) : StytchResult<T>()

    /**
     * Data class that can hold a StytchException
     * @property exception provides information about what went wrong during an API call
     */
    public data class Error(val exception: StytchError) : StytchResult<Nothing>()
}

internal fun <T> StytchResult<T>.getValueOrThrow(): T = when (this) {
    is StytchResult.Success -> value
    is StytchResult.Error -> throw exception
}
