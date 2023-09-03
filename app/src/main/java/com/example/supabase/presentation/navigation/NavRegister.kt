package com.example.supabase.presentation.navigation

import ShowUserScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.supabase.presentation.feature.listuser.ListUserScreen
import com.example.supabase.presentation.feature.showuser.UpdateUserScreen

fun NavGraphBuilder.navRegistration(navController: NavController) {

    composable(ListUserDestination.route) {
        ListUserScreen(
            navController = navController
        )
    }

    composable(
        route = "${ShowUserDestination.route}/{${ShowUserDestination.userUUID}}",
        arguments = ShowUserDestination.arguments
    ) {
        ShowUserScreen(
            navController = navController
        )
    }

    composable(
        route = "${UpdateUserDestination.route}/{${UpdateUserDestination.userUUID}}",
        arguments = UpdateUserDestination.arguments
    ) {
        val userUUID =
            it.arguments?.getString(UpdateUserDestination.userUUID)
        UpdateUserScreen(
            navController = navController,
            userUUID = userUUID
        )
    }

}