package com.stevecampos.feature.accountdetail.presentation.contract

sealed interface AccountDetailEffect {
    data class CopyAccountNumber(val accountNumber: String) : AccountDetailEffect
    data class ShareAccountDetails(val text: String) : AccountDetailEffect

    sealed interface Navigation : AccountDetailEffect {
        data object GoToLogin : Navigation
    }
}
