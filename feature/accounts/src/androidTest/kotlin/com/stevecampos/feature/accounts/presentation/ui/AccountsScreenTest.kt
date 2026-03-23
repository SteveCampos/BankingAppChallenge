package com.stevecampos.feature.accounts.presentation.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stevecampos.core.ui.theme.BankingAppTheme
import com.stevecampos.domain.model.Account
import com.stevecampos.feature.accounts.presentation.contract.AccountsDialogAction
import com.stevecampos.feature.accounts.presentation.contract.AccountsDialogState
import com.stevecampos.feature.accounts.presentation.contract.AccountsItemState
import com.stevecampos.feature.accounts.presentation.contract.AccountsState
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenLoadingState_whenScreenIsRendered_thenLoadingComposableIsVisible() {
        // Arrange
        setAccountsContent(
            state = AccountsState(isLoading = true),
        )

        // Assert
        composeRule.onNodeWithTag("accounts_loading").fetchSemanticsNode()
    }

    @Test
    fun givenErrorItemState_whenScreenIsRendered_thenErrorComposableIsVisible() {
        // Arrange
        setAccountsContent(
            state = AccountsState(
                items = listOf(
                    AccountsItemState.ErrorItem("No se pudo obtener las cuentas"),
                ),
            ),
        )

        // Assert
        composeRule.onNodeWithTag("accounts_error_item_0").fetchSemanticsNode()
        composeRule.onNodeWithText("No se pudo obtener las cuentas").fetchSemanticsNode()
    }

    @Test
    fun givenDialogState_whenScreenIsRendered_thenDialogIsVisible() {
        // Arrange
        setAccountsContent(
            state = AccountsState(
                dialog = AccountsDialogState(
                    title = "Error",
                    message = "Ha ocurrido un error, vuelve a intentarlo.",
                    confirmText = "Reintentar",
                    action = AccountsDialogAction.RETRY_INITIAL_LOAD,
                ),
            ),
        )

        // Assert
        composeRule.onNodeWithText("Error").fetchSemanticsNode()
        composeRule.onNodeWithText("Ha ocurrido un error, vuelve a intentarlo.").fetchSemanticsNode()
        composeRule.onNodeWithText("Reintentar").fetchSemanticsNode()
    }

    @Test
    fun givenAccountsContentAndRefreshState_whenScreenIsRendered_thenCardsAndRefreshIndicatorAreVisible() {
        // Arrange
        setAccountsContent(
            state = AccountsState(
                isRefreshing = true,
                items = listOf(
                    AccountsItemState.AccountItem(sampleAccount()),
                ),
            ),
        )

        // Assert
        composeRule.onNodeWithTag("accounts_refresh_indicator").fetchSemanticsNode()
        composeRule.onNodeWithTag("accounts_card_1").fetchSemanticsNode()
        composeRule.onNodeWithText("Cuenta sueldo").fetchSemanticsNode()
    }

    private fun setAccountsContent(
        state: AccountsState,
    ) {
        composeRule.setContent {
            BankingAppTheme {
                AccountsScreen(
                    state = state,
                    effect = emptyFlow(),
                    onIntent = {},
                    onNavigation = {},
                )
            }
        }
    }

    private fun sampleAccount() = Account(
        id = "1",
        name = "Cuenta sueldo",
        accountNumber = "001-1234567890",
        balance = 3240.50,
        currency = "S/",
    )
}
