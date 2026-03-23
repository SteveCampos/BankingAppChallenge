package com.stevecampos.feature.accounts.presentation.contract

data class AccountsDialogState(
    val title: String,
    val message: String,
    val confirmText: String,
    val action: AccountsDialogAction,
)

enum class AccountsDialogAction {
    RETRY_INITIAL_LOAD,
    DISMISS,
}
