package com.stevecampos.feature.accounts.presentation.contract

import com.stevecampos.domain.model.MockBehavior

data class AccountsState(
    val isRefreshing: Boolean = false,
    val contentState: AccountsContentState = AccountsContentState.Empty,
    val dialog: AccountsDialogState? = null,
    val userName: String? = null,
    val getAccountsBehavior: MockBehavior = MockBehavior.SUCCESS,
    val refreshAccountsBehavior: MockBehavior = MockBehavior.SUCCESS,
    val getMovementsBehavior: MockBehavior = MockBehavior.SUCCESS,
)
