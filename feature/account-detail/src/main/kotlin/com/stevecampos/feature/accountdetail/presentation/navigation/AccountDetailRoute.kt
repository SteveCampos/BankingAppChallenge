package com.stevecampos.feature.accountdetail.presentation.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevecampos.feature.accountdetail.R
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailEffect
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailIntent
import com.stevecampos.feature.accountdetail.presentation.ui.AccountDetailScreen
import com.stevecampos.feature.accountdetail.presentation.viewmodel.AccountDetailViewModel

@Composable
fun AccountDetailRoute(
    accountId: String,
    onNavigation: (AccountDetailEffect.Navigation) -> Unit,
    viewModel: AccountDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(accountId) {
        viewModel.onIntent(AccountDetailIntent.OnScreenOpened(accountId))
    }

    AccountDetailScreen(
        state = state,
        effect = viewModel.effect,
        onIntent = viewModel::onIntent,
        onNavigation = onNavigation,
        onCopyAccountNumber = { accountNumber ->
            clipboardManager.setText(AnnotatedString(accountNumber))
        },
        onShareAccountDetails = { shareData ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    context.getString(
                        R.string.account_detail_share_message,
                        shareData.accountName,
                        context.getString(
                            R.string.account_detail_label_account,
                            shareData.accountNumber,
                        ),
                        context.getString(
                            R.string.account_detail_label_balance,
                            shareData.formattedBalance,
                        ),
                    ),
                )
            }
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    context.getString(R.string.account_detail_share_chooser_title),
                ),
            )
        },
    )
}
