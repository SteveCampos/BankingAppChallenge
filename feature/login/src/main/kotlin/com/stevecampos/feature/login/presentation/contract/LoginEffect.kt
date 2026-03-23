package com.stevecampos.feature.login.presentation.contract

sealed interface LoginEffect {
    sealed interface Navigation : LoginEffect {
        data object GoToHome : Navigation
    }
}
