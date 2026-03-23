package com.stevecampos.feature.accounts.presentation.contract

import com.stevecampos.domain.model.MockBehavior

sealed interface AccountsIntent {
    data object OnScreenOpened : AccountsIntent
    data object OnRefreshRequested : AccountsIntent
    data object OnRetryInitialLoadClicked : AccountsIntent
    data object OnDialogDismissed : AccountsIntent
    data object OnDialogConfirmClicked : AccountsIntent
    data class OnGetAccountsBehaviorChanged(val behavior: MockBehavior) : AccountsIntent
    data class OnRefreshAccountsBehaviorChanged(val behavior: MockBehavior) : AccountsIntent
}
