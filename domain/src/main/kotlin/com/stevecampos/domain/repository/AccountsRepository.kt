package com.stevecampos.domain.repository

import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.Movement

interface AccountsRepository {
    suspend fun getAccounts(): Result<List<Account>>

    suspend fun refreshAccounts(): Result<List<Account>>

    suspend fun getAccount(accountId: String): Result<Account>

    suspend fun getMovements(accountId: String): Result<List<Movement>>
}
