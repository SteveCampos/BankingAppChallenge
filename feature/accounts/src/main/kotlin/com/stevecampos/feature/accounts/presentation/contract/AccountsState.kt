package com.stevecampos.feature.accounts.presentation.contract

import com.stevecampos.domain.model.MockBehavior

data class AccountsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val items: List<AccountsItemState> = emptyList(),
    val dialog: AccountsDialogState? = null,
    val userName: String? = null,
    val getAccountsBehavior: MockBehavior = MockBehavior.SUCCESS,
    val refreshAccountsBehavior: MockBehavior = MockBehavior.SUCCESS,
    val getMovementsBehavior: MockBehavior = MockBehavior.SUCCESS,
) {
    val hasAccountsContent: Boolean
        get() = items.any { item -> item is AccountsItemState.AccountItem }
}
