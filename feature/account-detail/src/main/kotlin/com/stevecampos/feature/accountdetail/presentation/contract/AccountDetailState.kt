package com.stevecampos.feature.accountdetail.presentation.contract

data class AccountDetailState(
    val accountId: String? = null,
    val contentState: AccountDetailContentState = AccountDetailContentState.Empty,
)
