@file:Suppress("MaxLineLength")
/* ktlint-disable max-line-length */
package com.stytch.sdk.common.errors

import org.junit.Test

internal class StytchErrorTests {
    @Test
    fun `StytchAPIError has expected properties`() {
        val error = StytchAPIError(
            requestId = "request-id-1234",
            name = "error_name",
            description = "error_description",
            url = "https://stytch.com"
        )
        assert(error.requestId == "request-id-1234")
        assert(error.name == "error_name")
        assert(error.description == "error_description")
        assert(error.url == "https://stytch.com")
    }

    @Test
    fun `StytchAPISchemaError has expected properties`() {
        val error = StytchAPISchemaError(
            description = "a schema error occurred",
        )
        assert(error.name == "StytchAPISchemaError")
        assert(error.description == "a schema error occurred")
    }

    @Test
    fun `StytchAPIUnreachableError has expected properties`() {
        val underlyingException = RuntimeException("testing")
        val error = StytchAPIUnreachableError(
            description = "a schema error occurred",
            exception = underlyingException,
        )
        assert(error.name == "StytchAPIUnreachableError")
        assert(error.description == "a schema error occurred")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchSDKNotConfiguredError has expected properties`() {
        val error = StytchSDKNotConfiguredError("Test")
        assert(error.name == "sdk_not_configured")
        assert(error.description == "Test not configured. You must call `Test.configure(...)` before using any functionality of the SDK.")
    }

    @Test
    fun `StytchInternalError has expected properties`() {
        val underlyingException = RuntimeException("testing")
        val error1 = StytchInternalError(exception = underlyingException)
        assert(error1.name == "stytch_internal_error")
        assert(error1.exception == underlyingException)
        assert(error1.description == "An internal error has occurred. Please contact Stytch if this occurs.")
        val error2 = StytchInternalError(description = "test")
        assert(error2.name == "stytch_internal_error")
        assert(error2.exception == null)
        assert(error2.description == "test")
    }

    @Test
    fun `StytchMissingPKCEError has expected properties`() {
        val underlyingException = RuntimeException("testing")
        val error = StytchMissingPKCEError(underlyingException)
        assert(error.name == "missing_pkce")
        assert(error.description == "The PKCE code challenge or code verifier is missing. Make sure this flow is completed on the same device on which it was started.")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchFailedToCreateCodeChallengeError has expected properties`() {
        val underlyingException = RuntimeException("testing")
        val error = StytchFailedToCreateCodeChallengeError(underlyingException)
        assert(error.name == "failed_code_challenge")
        assert(error.description == "Failed to generate a PKCE code challenge")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchDeeplinkUnkownTokenTypeError has expected properties`() {
        val error = StytchDeeplinkUnkownTokenTypeError
        assert(error.name == "deeplink_unknown_token_type")
        assert(error.description == "The deeplink received has an unknown token type.")
    }

    @Test
    fun `StytchDeeplinkMissingTokenError has expected properties`() {
        val error = StytchDeeplinkMissingTokenError
        assert(error.name == "deeplink_missing_token")
        assert(error.description == "The deeplink received has a missing token value.")
    }

    @Test
    fun `StytchNoCurrentSessionError has expected properties`() {
        val error = StytchNoCurrentSessionError
        assert(error.name == "no_current_session")
        assert(error.description == "There is no session currently available.")
    }

    @Test
    fun `StytchNoBiometricsRegistrationError has expected properties`() {
        val error = StytchNoBiometricsRegistrationError
        assert(error.name == "no_biometrics_registration")
        assert(error.description == "There is no biometric registration available. Authenticate with another method and add a new biometric registration first.")
    }

    @Test
    fun `StytchKeystoreUnavailableError has expected properties`() {
        val error = StytchKeystoreUnavailableError
        assert(error.name == "keystore_unavailable")
        assert(error.description == "The Android keystore is unavailable on the device. Consider setting allowFallbackToCleartext to true.")
    }

    @Test
    fun `StytchMissingPublicKeyError has expected properties`() {
        val underlyingException = RuntimeException("test")
        val error = StytchMissingPublicKeyError(underlyingException)
        assert(error.name == "missing_public_key")
        assert(error.description == "Failed to retrieve the public key. Add a new biometric registration.")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchChallengeSigningFailed has expected properties`() {
        val underlyingException = RuntimeException("test")
        val error = StytchChallengeSigningFailed(underlyingException)
        assert(error.name == "challenge_signing_failed")
        assert(error.description == "Failed to sign the challenge with the key.")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchMissingAuthorizationCredentialIdTokenError has expected properties`() {
        val error = StytchMissingAuthorizationCredentialIdTokenError
        assert(error.name == "missing_authorization_credential_id_token")
        assert(error.description == "The authorization credential is missing an ID token.")
    }

    @Test
    fun `StytchInvalidAuthorizationCredentialError has expected properties`() {
        val error = StytchInvalidAuthorizationCredentialError
        assert(error.name == "invalid_authorization_credential")
        assert(error.description == "The authorization credential is invalid.")
    }

    @Test
    fun `StytchPasskeysNotSupportedError has expected properties`() {
        val error = StytchPasskeysNotSupportedError
        assert(error.name == "passkeys_unsupported")
        assert(error.description == "Passkeys are not supported on this device.")
    }

    @Test
    fun `StytchFailedToDecryptDataError has expected properties`() {
        val underlyingException = RuntimeException("test")
        val error = StytchFailedToDecryptDataError(underlyingException)
        assert(error.name == "failed_to_decrypt_data")
        assert(error.description == "Failed to decrypt user data")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchBiometricAuthenticationFailed has expected properties`() {
        val error = StytchBiometricAuthenticationFailed("Some reason from the device")
        assert(error.name == "biometrics_failed")
        assert(error.description == "Biometric authentication failed")
        assert(error.reason == "Some reason from the device")
    }

    @Test
    fun `StytchSDKUsageError has expected properties`() {
        val error = StytchSDKUsageError("You did something wrong")
        assert(error.name == "StytchSDKUsageError")
        assert(error.description == "You did something wrong")
    }
}
