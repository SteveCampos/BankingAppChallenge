package com.stevecampos.feature.accountdetail.presentation.contract

import com.stevecampos.domain.model.MockBehavior

data class AccountDetailState(
    val accountId: String? = null,
    val contentState: AccountDetailContentState = AccountDetailContentState.Empty,
    val getMovementsBehavior: MockBehavior = MockBehavior.SUCCESS,
)
