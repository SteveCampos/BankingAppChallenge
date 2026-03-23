package com.stevecampos.feature.login.presentation.contract

import com.stevecampos.domain.model.MockBehavior

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val debugLoginBehavior: MockBehavior = MockBehavior.SUCCESS,
) {
    val isLoginEnabled: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && !isLoading
}
