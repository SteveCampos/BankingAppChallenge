package com.stevecampos.feature.login.presentation.contract

import androidx.annotation.StringRes

data class LoginState(
    val username: String = DEFAULT_USERNAME,
    val password: String = DEFAULT_PASSWORD,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
) {
    val isLoginEnabled: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && !isLoading

    private companion object {
        const val DEFAULT_USERNAME = "userTest1"
        const val DEFAULT_PASSWORD = "passTest1"
    }
}
