package com.stevecampos.data.di

import com.stevecampos.data.local.EncryptedTokenStore
import com.stevecampos.data.repository.DefaultDebugScenarioRepository
import com.stevecampos.data.repository.DefaultSessionRepository
import com.stevecampos.data.repository.MockAccountsRepository
import com.stevecampos.data.repository.MockAuthRepository
import com.stevecampos.domain.repository.AccountsRepository
import com.stevecampos.domain.repository.AuthRepository
import com.stevecampos.domain.repository.DebugScenarioRepository
import com.stevecampos.domain.repository.SessionRepository
import com.stevecampos.domain.repository.TokenStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        mockAuthRepository: MockAuthRepository,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAccountsRepository(
        mockAccountsRepository: MockAccountsRepository,
    ): AccountsRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        defaultSessionRepository: DefaultSessionRepository,
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindDebugScenarioRepository(
        defaultDebugScenarioRepository: DefaultDebugScenarioRepository,
    ): DebugScenarioRepository

    @Binds
    @Singleton
    abstract fun bindTokenStore(
        encryptedTokenStore: EncryptedTokenStore,
    ): TokenStore
}
