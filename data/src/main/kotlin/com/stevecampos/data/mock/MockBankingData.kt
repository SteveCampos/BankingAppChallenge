package com.stevecampos.data.mock

import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.Movement
import com.stevecampos.domain.model.MovementType

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

    val movementsByAccount: Map<String, List<Movement>> = mapOf(
        "001" to listOf(
            Movement("m001", "Abono de nomina", "Deposito recibido", 3500.00, "23 Mar 2026", MovementType.CREDIT),
            Movement("m002", "Pago de servicio", "Internet hogar", 149.90, "22 Mar 2026", MovementType.DEBIT),
            Movement("m003", "Transferencia enviada", "A cuenta de Juan Perez", 420.00, "21 Mar 2026", MovementType.DEBIT),
            Movement("m004", "Compra con tarjeta", "Supermercado", 286.35, "20 Mar 2026", MovementType.DEBIT),
        ),
        "002" to listOf(
            Movement("m005", "Interes ganado", "Rendimiento mensual", 18.40, "23 Mar 2026", MovementType.CREDIT),
            Movement("m006", "Deposito", "Transferencia recibida", 800.00, "19 Mar 2026", MovementType.CREDIT),
            Movement("m007", "Retiro", "Cajero automatico", 200.00, "18 Mar 2026", MovementType.DEBIT),
        ),
        "003" to listOf(
            Movement("m008", "Compra internacional", "Suscripcion software", 12.99, "21 Mar 2026", MovementType.DEBIT),
            Movement("m009", "Transferencia recibida", "Pago freelance", 150.00, "18 Mar 2026", MovementType.CREDIT),
        ),
    )
}
