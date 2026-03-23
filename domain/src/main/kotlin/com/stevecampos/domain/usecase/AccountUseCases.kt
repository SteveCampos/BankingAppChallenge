package com.stevecampos.domain.usecase

import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.Movement
import com.stevecampos.domain.repository.AccountsRepository
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val accountsRepository: AccountsRepository,
) {
    suspend operator fun invoke(): Result<List<Account>> = accountsRepository.getAccounts()
}

class RefreshAccountsUseCase @Inject constructor(
    private val accountsRepository: AccountsRepository,
) {
    suspend operator fun invoke(): Result<List<Account>> = accountsRepository.refreshAccounts()
}

class GetAccountUseCase @Inject constructor(
    private val accountsRepository: AccountsRepository,
) {
    suspend operator fun invoke(accountId: String): Result<Account> = accountsRepository.getAccount(accountId)
}

class GetAccountMovementsUseCase @Inject constructor(
    private val accountsRepository: AccountsRepository,
) {
    suspend operator fun invoke(accountId: String): Result<List<Movement>> = accountsRepository.getMovements(accountId)
}
