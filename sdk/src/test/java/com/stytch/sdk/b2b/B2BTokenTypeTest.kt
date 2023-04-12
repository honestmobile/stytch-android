package com.stytch.sdk.b2b

import org.junit.Test

internal class B2BTokenTypeTest {
    @Test
    fun `B2BTokenType fromString returns expected values`() {
        assert(B2BTokenType.fromString("multi_tenant_magic_links") == B2BTokenType.MULTI_TENANT_MAGIC_LINKS)
        assert(B2BTokenType.fromString("something_unexpected") == B2BTokenType.UNKNOWN)
    }
}
