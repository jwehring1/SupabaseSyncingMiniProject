package com.example.supabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.supabase.presentation.navigation.ListUserDestination
import com.example.supabase.presentation.navigation.navRegistration
import com.example.supabase.ui.theme.SupabaseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SupabaseTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = ListUserDestination.route) {
                    navRegistration(navController)
                }
            }
        }
    }

}
