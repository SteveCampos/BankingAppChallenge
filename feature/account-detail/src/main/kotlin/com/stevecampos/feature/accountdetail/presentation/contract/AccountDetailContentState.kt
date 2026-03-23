package com.stevecampos.feature.accountdetail.presentation.contract

import androidx.annotation.StringRes
import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.Movement

sealed interface AccountDetailContentState {
    data object Empty : AccountDetailContentState
    data object Loading : AccountDetailContentState
    data class Content(
        val account: Account,
        val movements: List<Movement>,
    ) : AccountDetailContentState
    data class Error(@StringRes val messageRes: Int) : AccountDetailContentState
}
