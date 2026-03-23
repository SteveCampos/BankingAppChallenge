package com.stevecampos.bankingapp.home

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stevecampos.core.ui.theme.BankingAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeShellScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenProductosSelected_whenScreenIsRendered_thenBottomNavigationAndProductsContentAreVisible() {
        // Arrange
        setHomeShellContent(selectedSection = HomeSection.PRODUCTOS)

        // Assert
        composeRule.onNodeWithTag("home_bottom_navigation").fetchSemanticsNode()
        composeRule.onNodeWithText("Productos").fetchSemanticsNode()
        composeRule.onNodeWithText("Operaciones").fetchSemanticsNode()
        composeRule.onNodeWithTag("products_content").fetchSemanticsNode()
    }

    @Test
    fun givenOperacionesSelected_whenScreenIsRendered_thenPlaceholderIsVisible() {
        // Arrange
        setHomeShellContent(selectedSection = HomeSection.OPERACIONES)

        // Assert
        composeRule.onNodeWithTag("operations_placeholder").fetchSemanticsNode()
        composeRule.onNodeWithText(
            "Operaciones estará disponible en una próxima iteración.",
        ).fetchSemanticsNode()
    }

    private fun setHomeShellContent(
        selectedSection: HomeSection,
    ) {
        composeRule.setContent {
            BankingAppTheme {
                HomeShellScreen(
                    selectedSection = selectedSection,
                    onSectionSelected = {},
                    productsContent = {
                        Box(modifier = Modifier.testTag("products_content"))
                    },
                )
            }
        }
    }
}
