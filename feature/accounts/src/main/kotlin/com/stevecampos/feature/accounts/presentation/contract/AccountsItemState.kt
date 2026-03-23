package com.stevecampos.feature.accounts.presentation.contract

import com.stevecampos.domain.model.Account

sealed interface AccountsItemState {
    data class AccountItem(val account: Account) : AccountsItemState
    data class ErrorItem(val message: String) : AccountsItemState
}
