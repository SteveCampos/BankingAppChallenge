package com.stevecampos.feature.accountdetail.presentation.contract

import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.Movement

sealed interface AccountDetailContentState {
    data object Empty : AccountDetailContentState
    data object Loading : AccountDetailContentState
    data class Content(
        val account: Account,
        val movements: List<Movement>,
    ) : AccountDetailContentState
    data class Error(val message: String) : AccountDetailContentState
}
