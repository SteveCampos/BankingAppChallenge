package com.stevecampos.feature.accountdetail.presentation.contract

sealed interface AccountDetailIntent {
    data class OnScreenOpened(val accountId: String) : AccountDetailIntent
    data object OnRetryClicked : AccountDetailIntent
    data object OnCopyAccountNumberClicked : AccountDetailIntent
    data object OnShareAccountDetailsClicked : AccountDetailIntent
}
