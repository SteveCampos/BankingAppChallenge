package com.stevecampos.feature.accounts.presentation.contract

import androidx.annotation.StringRes
import com.stevecampos.domain.model.Account

sealed interface AccountsContentState {
    data object Empty : AccountsContentState
    data object Loading : AccountsContentState
    data class Content(val accounts: List<Account>) : AccountsContentState
    data class Error(@StringRes val messageRes: Int) : AccountsContentState
}
