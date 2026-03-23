package com.stevecampos.data.mock

import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.Movement
import com.stevecampos.domain.model.MovementType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object MockBankingData {
    val validUsers: Map<String, String> = mapOf(
        "userTest1" to "passTest1",
        "User@test" to "TestPass_",
        "user123&" to "123456",
    )

    val accounts: List<Account> = listOf(
        Account(
            id = "001",
            name = "Cuenta Sueldo",
            accountNumber = "001-12345678-90",
            balance = 15890.45,
            currency = "S/",
        ),
        Account(
            id = "002",
            name = "Cuenta Ahorros",
            accountNumber = "001-87654321-09",
            balance = 5240.18,
            currency = "S/",
        ),
        Account(
            id = "003",
            name = "Cuenta Dolares",
            accountNumber = "001-45671234-55",
            balance = 1420.65,
            currency = "$",
        ),
    )

    val movementsByAccount: Map<String, List<Movement>> = accounts.associate { account ->
        account.id to defaultMovements()
    }

    private fun defaultMovements(): List<Movement> = listOf(
        Movement(
            id = "m001",
            title = "Transferencia",
            description = "",
            amount = 6.10,
            date = "Hoy",
            type = MovementType.CREDIT,
        ),
        Movement(
            id = "m002",
            title = "Plin",
            description = "",
            amount = 10.00,
            date = formatDate(LocalDate.now().minusDays(10)),
            type = MovementType.DEBIT,
        ),
    )

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "PE"))
        return date.format(formatter).replaceFirstChar { char ->
            char.uppercase()
        }
    }
}
