package com.stevecampos.feature.login.presentation.contract

data class LoginState(
    val username: String = DEFAULT_USERNAME,
    val password: String = DEFAULT_PASSWORD,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val isLoginEnabled: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && !isLoading

    private companion object {
        const val DEFAULT_USERNAME = "userTest1"
        const val DEFAULT_PASSWORD = "passTest1"
    }
}
