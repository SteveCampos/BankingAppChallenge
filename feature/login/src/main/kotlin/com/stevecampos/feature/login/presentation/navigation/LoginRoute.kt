package com.stevecampos.feature.login.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevecampos.feature.login.presentation.contract.LoginEffect
import com.stevecampos.feature.login.presentation.ui.LoginScreen
import com.stevecampos.feature.login.presentation.viewmodel.LoginViewModel

@Composable
fun LoginRoute(
    onNavigation: (LoginEffect.Navigation) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginScreen(
        state = state,
        effect = viewModel.effect,
        onIntent = viewModel::onIntent,
        onNavigation = onNavigation,
    )
}
