package com.stevecampos.bankingapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stevecampos.bankingapp.home.HomePlaceholderRoute
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
            HomePlaceholderRoute()
        }
    }
}
