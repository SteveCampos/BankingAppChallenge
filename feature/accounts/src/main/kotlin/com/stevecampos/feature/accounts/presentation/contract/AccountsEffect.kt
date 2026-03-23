package com.stevecampos.feature.accounts.presentation.contract

sealed interface AccountsEffect {
    sealed interface Navigation : AccountsEffect {
        data object GoToLogin : Navigation
    }
}
