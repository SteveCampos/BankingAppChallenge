package com.stevecampos.feature.accounts.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stevecampos.core.ui.component.AppMessageDialog
import com.stevecampos.core.ui.component.DebugBehaviorRow
import com.stevecampos.core.ui.component.DebugControlsCard
import com.stevecampos.core.ui.component.FullscreenLoading
import com.stevecampos.core.ui.theme.BankingAppTheme
import com.stevecampos.core.ui.util.formatCurrency
import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.MockBehavior
import com.stevecampos.feature.accounts.R
import com.stevecampos.feature.accounts.presentation.contract.AccountsDialogAction
import com.stevecampos.feature.accounts.presentation.contract.AccountsContentState
import com.stevecampos.feature.accounts.presentation.contract.AccountsDialogState
import com.stevecampos.feature.accounts.presentation.contract.AccountsEffect
import com.stevecampos.feature.accounts.presentation.contract.AccountsIntent
import com.stevecampos.feature.accounts.presentation.contract.AccountsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountsScreen(
    state: AccountsState,
    effect: Flow<AccountsEffect>,
    onIntent: (AccountsIntent) -> Unit,
    onNavigation: (AccountsEffect.Navigation) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onIntent(AccountsIntent.OnRefreshRequested) },
    )

    LaunchedEffect(effect) {
        effect.collect { emittedEffect ->
            when (emittedEffect) {
                is AccountsEffect.Navigation -> onNavigation(emittedEffect)
            }
        }
    }

    LaunchedEffect(Unit) {
        onIntent(AccountsIntent.OnScreenOpened)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .testTag("accounts_screen"),
    ) {
        AccountsContent(
            state = state,
            onIntent = onIntent,
            modifier = Modifier.fillMaxSize(),
        )

        if (
            state.contentState is AccountsContentState.Loading ||
            state.isRefreshing
        ) {
            FullscreenLoading(
                modifier = Modifier.testTag("accounts_loading"),
                message = if (state.isRefreshing) {
                    stringResource(R.string.accounts_loading_refresh)
                } else {
                    stringResource(R.string.accounts_loading_initial)
                },
            )
        }
    }

    state.dialog?.let { dialogState ->
        AppMessageDialog(
            title = stringResource(dialogState.titleRes),
            message = stringResource(dialogState.messageRes),
            confirmText = stringResource(dialogState.confirmTextRes),
            onConfirm = { onIntent(AccountsIntent.OnDialogConfirmClicked) },
            onDismiss = { onIntent(AccountsIntent.OnDialogDismissed) },
        )
    }
}

@Composable
private fun AccountsContent(
    state: AccountsState,
    onIntent: (AccountsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("accounts_list"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            accountsHeader(userName = state.userName)
            accountsContentByState(
                contentState = state.contentState,
                onAccountClick = { accountId ->
                    onIntent(AccountsIntent.OnAccountClicked(accountId))
                },
            )

            item {
                DebugControlsCard(
                    title = stringResource(R.string.accounts_debug_title),
                    modifier = Modifier.testTag("accounts_debug_controls"),
                ) {
                    DebugBehaviorRow(
                        label = stringResource(R.string.accounts_debug_get_accounts),
                        selectedValue = mockBehaviorLabel(state.getAccountsBehavior),
                        successText = stringResource(R.string.accounts_debug_behavior_success),
                        errorText = stringResource(R.string.accounts_debug_behavior_error),
                        onSuccessSelected = {
                            onIntent(AccountsIntent.OnGetAccountsBehaviorChanged(MockBehavior.SUCCESS))
                        },
                        onErrorSelected = {
                            onIntent(AccountsIntent.OnGetAccountsBehaviorChanged(MockBehavior.ERROR))
                        },
                    )
                    DebugBehaviorRow(
                        label = stringResource(R.string.accounts_debug_refresh_accounts),
                        selectedValue = mockBehaviorLabel(state.refreshAccountsBehavior),
                        successText = stringResource(R.string.accounts_debug_behavior_success),
                        errorText = stringResource(R.string.accounts_debug_behavior_error),
                        onSuccessSelected = {
                            onIntent(AccountsIntent.OnRefreshAccountsBehaviorChanged(MockBehavior.SUCCESS))
                        },
                        onErrorSelected = {
                            onIntent(AccountsIntent.OnRefreshAccountsBehaviorChanged(MockBehavior.ERROR))
                        },
                    )
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.accountsHeader(userName: String?) {
    item {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.accounts_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = userName?.let { greetingName ->
                    stringResource(R.string.accounts_greeting, greetingName)
                } ?: stringResource(R.string.accounts_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.accountsContentByState(
    contentState: AccountsContentState,
    onAccountClick: (String) -> Unit,
) {
    when (contentState) {
        AccountsContentState.Empty,
        AccountsContentState.Loading -> Unit

        is AccountsContentState.Content -> {
            items(
                items = contentState.accounts,
                key = { account -> account.id },
            ) { account ->
                AccountCard(
                    account = account,
                    onClick = onAccountClick,
                    modifier = Modifier.testTag("accounts_card_${account.id}"),
                )
            }
        }

        is AccountsContentState.Error -> {
            item {
                AccountsErrorItem(
                    message = stringResource(contentState.messageRes),
                    modifier = Modifier.testTag("accounts_error"),
                )
            }
        }
    }
}

@Composable
private fun AccountCard(
    account: Account,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(account.id) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = account.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formatCurrency(
                    amount = account.balance,
                    currency = account.currency,
                ),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.accounts_account_number, account.accountNumber),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AccountsErrorItem(
    message: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.accounts_unavailable_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountsScreenPreview() {
    BankingAppTheme {
        AccountsScreen(
            state = AccountsState(
                contentState = AccountsContentState.Content(
                    listOf(
                        Account(
                            id = "1",
                            name = "Cuenta sueldo",
                            accountNumber = "001-1234567890",
                            balance = 3240.50,
                            currency = "S/",
                        ),
                    ),
                ),
                dialog = AccountsDialogState(
                    titleRes = R.string.accounts_dialog_error_title,
                    messageRes = R.string.accounts_dialog_load_message,
                    confirmTextRes = R.string.accounts_dialog_retry,
                    action = AccountsDialogAction.RETRY_INITIAL_LOAD,
                ),
                userName = "userTest1",
            ),
            effect = emptyFlow(),
            onIntent = {},
            onNavigation = {},
        )
    }
}

@Composable
private fun mockBehaviorLabel(behavior: MockBehavior): String = when (behavior) {
    MockBehavior.SUCCESS -> stringResource(R.string.accounts_debug_behavior_success)
    MockBehavior.ERROR -> stringResource(R.string.accounts_debug_behavior_error)
}
