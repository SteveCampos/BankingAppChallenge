package com.stevecampos.bankingapp.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomePlaceholderRoute(
    viewModel: HomePlaceholderViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomePlaceholderScreen(state = state)
}

@Composable
fun HomePlaceholderScreen(
    state: HomePlaceholderState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Home temporal",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "La vertical slice de login ya está conectada. La siguiente iteración continuará con cuentas.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Usuario autenticado: ${state.username.ifBlank { "No disponible" }}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Token mock guardado: ${state.accessTokenPreview.ifBlank { "No disponible" }}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
