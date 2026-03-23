package com.stevecampos.feature.login.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stevecampos.core.ui.component.FullscreenLoading
import com.stevecampos.core.ui.theme.BankingAppTheme
import com.stevecampos.feature.login.presentation.contract.LoginEffect
import com.stevecampos.feature.login.presentation.contract.LoginIntent
import com.stevecampos.feature.login.presentation.contract.LoginState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun LoginScreen(
    state: LoginState,
    effect: Flow<LoginEffect>,
    onIntent: (LoginIntent) -> Unit,
    onNavigation: (LoginEffect.Navigation) -> Unit,
    modifier: Modifier = Modifier,
    appName: String? = null,
) {
    val context = LocalContext.current
    val resolvedAppName = appName ?: remember(context) {
        context.applicationInfo.loadLabel(context.packageManager).toString()
    }

    LaunchedEffect(effect) {
        effect.collect { emittedEffect ->
            when (emittedEffect) {
                is LoginEffect.Navigation -> onNavigation(emittedEffect)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = resolvedAppName,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ingresa para continuar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = state.username,
                onValueChange = { onIntent(LoginIntent.OnUsernameChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_username"),
                label = { Text("Usuario") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = { onIntent(LoginIntent.OnPasswordChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password"),
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                trailingIcon = {
                    IconButton(
                        onClick = { onIntent(LoginIntent.OnPasswordVisibilityClicked) },
                    ) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            contentDescription = if (state.isPasswordVisible) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                        )
                    }
                },
            )
            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.errorMessage,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onIntent(LoginIntent.OnLoginClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_button"),
                enabled = state.isLoginEnabled,
            ) {
                Text("Ingresar")
            }

            // Reservado para volver a mostrar referencias de usuarios mock si el challenge lo requiere.
            // Spacer(modifier = Modifier.height(24.dp))
            // MockUsersReference()
        }

        if (state.isLoading) {
            FullscreenLoading(message = "Validando credenciales...")
        }
    }
}

// @Composable
// private fun MockUsersReference(modifier: Modifier = Modifier) {
//     Surface(
//         modifier = modifier.fillMaxWidth(),
//         shape = MaterialTheme.shapes.medium,
//         tonalElevation = 2.dp,
//     ) {
//         Column(
//             modifier = Modifier.padding(16.dp),
//             verticalArrangement = Arrangement.spacedBy(8.dp),
//         ) {
//             Text(
//                 text = "Usuarios válidos",
//                 style = MaterialTheme.typography.titleSmall,
//             )
//             Text("userTest1 / passTest1")
//             Text("User@test / TestPass_")
//             Text("user123& / 123456")
//         }
//     }
// }

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    BankingAppTheme {
        LoginScreen(
            state = LoginState(),
            effect = emptyFlow(),
            onIntent = {},
            onNavigation = {},
            appName = "Banking App Challenge",
        )
    }
}
