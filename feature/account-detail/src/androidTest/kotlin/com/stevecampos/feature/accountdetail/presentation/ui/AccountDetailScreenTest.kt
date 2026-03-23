package com.stevecampos.feature.accountdetail.presentation.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stevecampos.core.ui.theme.BankingAppTheme
import com.stevecampos.domain.model.Account
import com.stevecampos.domain.model.Movement
import com.stevecampos.domain.model.MovementType
import com.stevecampos.feature.accountdetail.R
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailContentState
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailState
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenLoadingState_whenScreenIsRendered_thenFullscreenLoadingIsVisible() {
        // Arrange
        setAccountDetailContent(
            state = AccountDetailState(contentState = AccountDetailContentState.Loading),
        )

        // Assert
        composeRule.onNodeWithTag("account_detail_loading").fetchSemanticsNode()
    }

    @Test
    fun givenContentState_whenScreenIsRendered_thenAccountSummaryAndMovementsAreVisible() {
        // Arrange
        setAccountDetailContent(
            state = AccountDetailState(
                contentState = AccountDetailContentState.Content(
                    account = sampleAccount(),
                    movements = sampleMovements(),
                ),
            ),
        )

        // Assert
        composeRule.onNodeWithText("Cuenta Sueldo").fetchSemanticsNode()
        composeRule.onNodeWithText("Movimientos").fetchSemanticsNode()
        composeRule.onNodeWithTag("movement_m001").fetchSemanticsNode()
        composeRule.onNodeWithText("Transferencia").fetchSemanticsNode()
        composeRule.onNodeWithText("S/ + 6.10").fetchSemanticsNode()
        composeRule.onNodeWithText("Plin").fetchSemanticsNode()
        composeRule.onNodeWithText("S/ - 10.00").fetchSemanticsNode()
        composeRule.onNodeWithTag("copy_account_button").fetchSemanticsNode()
    }

    @Test
    fun givenErrorState_whenScreenIsRendered_thenErrorMessageIsVisible() {
        // Arrange
        setAccountDetailContent(
            state = AccountDetailState(
                contentState = AccountDetailContentState.Error(
                    R.string.account_detail_error_movements,
                ),
            ),
        )

        // Assert
        composeRule.onNodeWithTag("account_detail_error").fetchSemanticsNode()
        composeRule.onNodeWithText("No se pudieron obtener los movimientos").fetchSemanticsNode()
        composeRule.onNodeWithText("Reintentar").fetchSemanticsNode()
    }

    private fun setAccountDetailContent(
        state: AccountDetailState,
    ) {
        composeRule.setContent {
            BankingAppTheme {
                AccountDetailScreen(
                    state = state,
                    effect = emptyFlow(),
                    onIntent = {},
                    onNavigation = {},
                    onCopyAccountNumber = {},
                    onShareAccountDetails = {},
                )
            }
        }
    }

    private fun sampleAccount() = Account(
        id = "001",
        name = "Cuenta Sueldo",
        accountNumber = "001-12345678-90",
        balance = 15890.45,
        currency = "S/",
    )

    private fun sampleMovements() = listOf(
        Movement(
            id = "m001",
            title = "Transferencia",
            description = "",
            amount = 6.10,
            date = "Hoy",
            type = MovementType.CREDIT,
        ),
        Movement(
            id = "m002",
            title = "Plin",
            description = "",
            amount = 10.00,
            date = "13 Mar 2026",
            type = MovementType.DEBIT,
        ),
    )
}
