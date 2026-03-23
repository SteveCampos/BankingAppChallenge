package com.stevecampos.feature.login.presentation.contract

import com.stevecampos.domain.model.MockBehavior

sealed interface LoginIntent {
    data class OnUsernameChanged(val value: String) : LoginIntent
    data class OnPasswordChanged(val value: String) : LoginIntent
    data object OnPasswordVisibilityClicked : LoginIntent
    data object OnLoginClicked : LoginIntent
    data class OnDebugBehaviorChanged(val behavior: MockBehavior) : LoginIntent
}
