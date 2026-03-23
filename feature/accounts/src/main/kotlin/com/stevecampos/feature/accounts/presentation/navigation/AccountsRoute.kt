package com.stevecampos.feature.accounts.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevecampos.feature.accounts.presentation.contract.AccountsEffect
import com.stevecampos.feature.accounts.presentation.ui.AccountsScreen
import com.stevecampos.feature.accounts.presentation.viewmodel.AccountsViewModel

@Composable
fun AccountsRoute(
    onNavigation: (AccountsEffect.Navigation) -> Unit,
    viewModel: AccountsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AccountsScreen(
        state = state,
        effect = viewModel.effect,
        onIntent = viewModel::onIntent,
        onNavigation = onNavigation,
    )
}
