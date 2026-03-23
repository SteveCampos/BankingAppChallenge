package com.stevecampos.feature.accounts.presentation.contract

import androidx.annotation.StringRes

data class AccountsDialogState(
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int,
    @StringRes val confirmTextRes: Int,
    val action: AccountsDialogAction,
)

enum class AccountsDialogAction {
    RETRY_INITIAL_LOAD,
    DISMISS,
}
