package com.example.supabase.presentation.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Destination {
    val route: String
    val title: String
}

object ListUserDestination : Destination {
    override val route = "list_user"
    override val title = "List User"
}

object ShowUserDestination : Destination {
    override val route = "show_user"
    override val title = "show User"
    const val userUUID = "user_uuid"
    val arguments = listOf(navArgument(name = userUUID) {
        type = NavType.StringType
    })
    fun createRouteWithParam(userUUID: String?) = "$route/${userUUID}"
}

object UpdateUserDestination : Destination {
    override val route = "update_user"
    override val title = "update User"
    const val userUUID = "user_uuid"
    val arguments = listOf(navArgument(name = userUUID) {
        type = NavType.StringType
    })
    fun createRouteWithParam(userUUID: String?) = "$route/${userUUID}"
}