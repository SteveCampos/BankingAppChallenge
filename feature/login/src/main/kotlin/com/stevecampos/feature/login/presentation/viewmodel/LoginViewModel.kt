package com.stevecampos.feature.login.presentation.viewmodel

import com.stevecampos.core.ui.mvi.MviViewModel
import com.stevecampos.core.ui.util.EmojiSanitizer
import com.stevecampos.domain.coroutines.IoDispatcher
import com.stevecampos.domain.model.DomainException
import com.stevecampos.domain.usecase.LoginUseCase
import com.stevecampos.feature.login.presentation.contract.LoginEffect
import com.stevecampos.feature.login.presentation.contract.LoginIntent
import com.stevecampos.feature.login.presentation.contract.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MviViewModel<LoginState, LoginIntent, LoginEffect>(
    initialState = LoginState(),
    defaultDispatcher = ioDispatcher,
) {

    override fun onIntent(intent: LoginIntent) {
        when (intent) {
            LoginIntent.OnLoginClicked -> submitLogin()
            LoginIntent.OnPasswordVisibilityClicked -> togglePasswordVisibility()
            is LoginIntent.OnPasswordChanged -> updatePassword(intent.value)
            is LoginIntent.OnUsernameChanged -> updateUsername(intent.value)
        }
    }

    private fun updateUsername(value: String) {
        updateState {
            copy(
                username = EmojiSanitizer.removeEmoji(value),
                errorMessage = null,
            )
        }
    }

    private fun updatePassword(value: String) {
        updateState {
            copy(
                password = EmojiSanitizer.removeEmoji(value),
                errorMessage = null,
            )
        }
    }

    private fun togglePasswordVisibility() {
        updateState {
            copy(isPasswordVisible = !isPasswordVisible)
        }
    }

    private fun submitLogin() {
        val currentState = state.value
        if (!currentState.isLoginEnabled) {
            return
        }

        updateState {
            copy(
                isLoading = true,
                errorMessage = null,
            )
        }

        executeUseCase(
            onSuccess = {
                updateState { copy(isLoading = false) }
                emitEffect(LoginEffect.Navigation.GoToHome)
            },
            onError = { throwable ->
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = when (throwable) {
                            DomainException.InvalidCredentials -> INVALID_CREDENTIALS_MESSAGE
                            else -> throwable.message ?: INVALID_CREDENTIALS_MESSAGE
                        },
                    )
                }
            },
        ) {
            loginUseCase(
                username = currentState.username,
                password = currentState.password,
            )
        }
    }

    private companion object {
        const val INVALID_CREDENTIALS_MESSAGE = "Usuario y/o contraseña incorrectos"
    }
}
