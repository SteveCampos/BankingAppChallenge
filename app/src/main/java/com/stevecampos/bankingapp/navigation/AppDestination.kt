package com.stevecampos.bankingapp.navigation

import kotlinx.serialization.Serializable

@Serializable
data object LoginDestination

@Serializable
data object HomeDestination

@Serializable
data class AccountDetailDestination(val accountId: String)
