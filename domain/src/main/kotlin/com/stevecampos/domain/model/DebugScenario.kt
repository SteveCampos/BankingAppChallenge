package com.stevecampos.domain.model

enum class DebugOperation(val label: String) {
    LOGIN("Login"),
    GET_ACCOUNTS("Obtener cuentas"),
    REFRESH_ACCOUNTS("Actualizar cuentas"),
    GET_MOVEMENTS("Obtener movimientos"),
}

enum class MockBehavior(val label: String) {
    SUCCESS("Success"),
    ERROR("Error"),
}
