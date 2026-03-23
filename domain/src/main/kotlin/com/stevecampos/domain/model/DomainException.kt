package com.stevecampos.domain.model

sealed class DomainException(
    message: String,
) : Exception(message) {
    data object Unauthorized : DomainException("Unauthorized")

    data object InvalidCredentials : DomainException("Usuario y/o contraseña incorrectos")
}
