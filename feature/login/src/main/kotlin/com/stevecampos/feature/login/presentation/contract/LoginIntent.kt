package com.stevecampos.feature.login.presentation.contract

sealed interface LoginIntent {
    data class OnUsernameChanged(val value: String) : LoginIntent
    data class OnPasswordChanged(val value: String) : LoginIntent
    data object OnPasswordVisibilityClicked : LoginIntent
    data object OnLoginClicked : LoginIntent
}
