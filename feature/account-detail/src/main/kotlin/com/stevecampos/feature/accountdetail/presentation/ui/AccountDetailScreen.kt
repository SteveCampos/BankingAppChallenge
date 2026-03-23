package com.stevecampos.feature.accountdetail.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stevecampos.core.ui.component.FullscreenLoading
import com.stevecampos.core.ui.theme.BankingAppTheme
import com.stevecampos.core.ui.util.formatCurrency
import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.Movement
import com.stevecampos.domain.model.MovementType
import com.stevecampos.feature.accountdetail.R
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailContentState
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailEffect
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailIntent
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun AccountDetailScreen(
    state: AccountDetailState,
    effect: Flow<AccountDetailEffect>,
    onIntent: (AccountDetailIntent) -> Unit,
    onNavigation: (AccountDetailEffect.Navigation) -> Unit,
    onCopyAccountNumber: (String) -> Unit,
    onShareAccountDetails: (AccountDetailEffect.ShareAccountDetails) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(effect) {
        effect.collect { emittedEffect ->
            when (emittedEffect) {
                is AccountDetailEffect.CopyAccountNumber -> {
                    onCopyAccountNumber(emittedEffect.accountNumber)
                }

                is AccountDetailEffect.Navigation -> onNavigation(emittedEffect)
                is AccountDetailEffect.ShareAccountDetails -> {
                    onShareAccountDetails(emittedEffect)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("account_detail_screen"),
    ) {
        AccountDetailContent(
            state = state,
            onIntent = onIntent,
            modifier = Modifier.fillMaxSize(),
        )

        if (state.contentState is AccountDetailContentState.Loading) {
            FullscreenLoading(
                modifier = Modifier.testTag("account_detail_loading"),
                message = stringResource(R.string.account_detail_loading),
            )
        }
    }
}

@Composable
private fun AccountDetailContent(
    state: AccountDetailState,
    onIntent: (AccountDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.testTag("account_detail_list"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (val contentState = state.contentState) {
            AccountDetailContentState.Empty,
            AccountDetailContentState.Loading -> Unit

            is AccountDetailContentState.Content -> {
                item {
                    AccountSummaryCard(
                        account = contentState.account,
                        onCopyClick = {
                            onIntent(AccountDetailIntent.OnCopyAccountNumberClicked)
                        },
                        onShareClick = {
                            onIntent(AccountDetailIntent.OnShareAccountDetailsClicked)
                        },
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.account_detail_movements_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                items(
                    items = contentState.movements,
                    key = { movement -> movement.id },
                ) { movement ->
                    MovementRow(
                        movement = movement,
                        currency = contentState.account.currency,
                        modifier = Modifier.testTag("movement_${movement.id}"),
                    )
                }
            }

            is AccountDetailContentState.Error -> {
                item {
                    AccountDetailError(
                        message = stringResource(contentState.messageRes),
                        onRetryClick = {
                            onIntent(AccountDetailIntent.OnRetryClicked)
                        },
                    )
                }
            }
        }

    }
}

@Composable
private fun AccountSummaryCard(
    account: Account,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = account.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formatCurrency(account.balance, account.currency),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.account_detail_account_number, account.accountNumber),
                style = MaterialTheme.typography.bodyLarge,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onCopyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("copy_account_button"),
                ) {
                    Text(stringResource(R.string.account_detail_copy_account))
                }
                Button(
                    onClick = onShareClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("share_account_button"),
                ) {
                    Text(stringResource(R.string.account_detail_share_detail))
                }
            }
        }
    }
}

@Composable
private fun MovementRow(
    movement: Movement,
    currency: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = movement.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (movement.description.isNotBlank()) {
                Text(
                    text = movement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = movement.date,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = movementAmount(
                    movement = movement,
                    currency = currency,
                ),
                style = MaterialTheme.typography.titleMedium,
                color = if (movement.type == MovementType.CREDIT) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
        }
    }
}

@Composable
private fun AccountDetailError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("account_detail_error"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.account_detail_error_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.account_detail_retry))
            }
        }
    }
}

private fun movementAmount(
    movement: Movement,
    currency: String,
): String {
    val prefix = if (movement.type == MovementType.CREDIT) "+" else "-"
    val formattedAmount = formatCurrency(
        amount = movement.amount,
        currency = currency,
    )
    val numericAmount = formattedAmount.removePrefix("$currency ")
    return "$currency $prefix $numericAmount"
}

@Preview(showBackground = true)
@Composable
private fun AccountDetailScreenPreview() {
    BankingAppTheme {
        AccountDetailScreen(
            state = AccountDetailState(
                contentState = AccountDetailContentState.Content(
                    account = Account(
                        id = "001",
                        name = "Cuenta Sueldo",
                        accountNumber = "001-12345678-90",
                        balance = 15890.45,
                        currency = "S/",
                    ),
                    movements = listOf(
                        Movement(
                            id = "m001",
                            title = "Transferencia",
                            description = "",
                            amount = 6.10,
                            date = "Hoy",
                            type = MovementType.CREDIT,
                        ),
                    ),
                ),
            ),
            effect = emptyFlow(),
            onIntent = {},
            onNavigation = {},
            onCopyAccountNumber = {},
            onShareAccountDetails = {},
        )
    }
}
