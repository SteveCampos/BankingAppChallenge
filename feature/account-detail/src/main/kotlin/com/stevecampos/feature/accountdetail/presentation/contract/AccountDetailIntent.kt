package com.stevecampos.feature.accountdetail.presentation.contract

import com.stevecampos.domain.model.MockBehavior

sealed interface AccountDetailIntent {
    data class OnScreenOpened(val accountId: String) : AccountDetailIntent
    data object OnRetryClicked : AccountDetailIntent
    data object OnCopyAccountNumberClicked : AccountDetailIntent
    data object OnShareAccountDetailsClicked : AccountDetailIntent
    data class OnGetMovementsBehaviorChanged(val behavior: MockBehavior) : AccountDetailIntent
}
