package com.stevecampos.bankingapp.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.stevecampos.bankingapp.R
import com.stevecampos.feature.accounts.presentation.contract.AccountsEffect
import com.stevecampos.feature.accounts.presentation.navigation.AccountsRoute

@Composable
fun HomeShellRoute(
    onAccountsNavigation: (AccountsEffect.Navigation) -> Unit,
) {
    var selectedSection by rememberSaveable { mutableStateOf(HomeSection.PRODUCTOS) }

    HomeShellScreen(
        selectedSection = selectedSection,
        onSectionSelected = { selectedSection = it },
        productsContent = {
            AccountsRoute(onNavigation = onAccountsNavigation)
        },
    )
}

@Composable
fun HomeShellScreen(
    selectedSection: HomeSection,
    onSectionSelected: (HomeSection) -> Unit,
    productsContent: @Composable () -> Unit,
    operationsContent: @Composable () -> Unit = {
        OperationsPlaceholderScreen()
    },
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("home_bottom_navigation"),
            ) {
                NavigationBarItem(
                    selected = selectedSection == HomeSection.PRODUCTOS,
                    onClick = { onSectionSelected(HomeSection.PRODUCTOS) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = stringResource(R.string.home_products),
                        )
                    },
                    label = { Text(stringResource(R.string.home_products)) },
                )
                NavigationBarItem(
                    selected = selectedSection == HomeSection.OPERACIONES,
                    onClick = { onSectionSelected(HomeSection.OPERACIONES) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Build,
                            contentDescription = stringResource(R.string.home_operations),
                        )
                    },
                    label = { Text(stringResource(R.string.home_operations)) },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (selectedSection) {
                HomeSection.PRODUCTOS -> productsContent()
                HomeSection.OPERACIONES -> operationsContent()
            }
        }
    }
}

@Composable
private fun OperationsPlaceholderScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("operations_placeholder"),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.home_operations_placeholder),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

enum class HomeSection {
    PRODUCTOS,
    OPERACIONES,
}
