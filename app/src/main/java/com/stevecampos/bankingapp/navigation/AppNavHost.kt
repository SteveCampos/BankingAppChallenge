package com.stevecampos.bankingapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.stevecampos.bankingapp.home.HomeShellRoute
import com.stevecampos.feature.accounts.presentation.contract.AccountsEffect
import com.stevecampos.feature.accountdetail.presentation.contract.AccountDetailEffect
import com.stevecampos.feature.accountdetail.presentation.navigation.AccountDetailRoute
import com.stevecampos.feature.login.presentation.contract.LoginEffect
import com.stevecampos.feature.login.presentation.navigation.LoginRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = LoginDestination,
    ) {
        composable<LoginDestination> {
            LoginRoute(
                onNavigation = { navigationEffect ->
                    when (navigationEffect) {
                        LoginEffect.Navigation.GoToHome -> {
                            navController.navigate(HomeDestination) {
                                popUpTo<LoginDestination> {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                },
            )
        }

        composable<HomeDestination> {
            HomeShellRoute(
                onAccountsNavigation = { navigationEffect ->
                    when (navigationEffect) {
                        is AccountsEffect.Navigation.GoToAccountDetail -> {
                            navController.navigate(
                                AccountDetailDestination(accountId = navigationEffect.accountId),
                            )
                        }

                        AccountsEffect.Navigation.GoToLogin -> {
                            navController.navigate(LoginDestination) {
                                popUpTo<HomeDestination> {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                },
            )
        }

        composable<AccountDetailDestination> { backStackEntry ->
            val destination = backStackEntry.toRoute<AccountDetailDestination>()
            AccountDetailRoute(
                accountId = destination.accountId,
                onNavigation = { navigationEffect ->
                    when (navigationEffect) {
                        AccountDetailEffect.Navigation.GoToLogin -> {
                            navController.navigate(LoginDestination) {
                                popUpTo<HomeDestination> {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                },
            )
        }
    }
}
