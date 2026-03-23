package com.stevecampos.domain.model

data class Account(
    val id: String,
    val name: String,
    val accountNumber: String,
    val balance: Double,
    val currency: String,
)

data class Movement(
    val id: String,
    val title: String,
    val description: String,
    val amount: Double,
    val date: String,
    val type: MovementType,
)

enum class MovementType {
    CREDIT,
    DEBIT,
}
