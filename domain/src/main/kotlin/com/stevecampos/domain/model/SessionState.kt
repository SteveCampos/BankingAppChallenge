package com.stevecampos.domain.model

data class AuthSession(
    val accessToken: String,
    val username: String,
    val issuedAtMillis: Long,
    val expiresAtMillis: Long,
    val lastActivityAtMillis: Long,
)

data class SessionState(
    val session: AuthSession? = null,
) {
    val isAuthenticated: Boolean
        get() = session != null
}
