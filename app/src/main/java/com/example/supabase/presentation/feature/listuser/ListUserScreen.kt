package com.example.supabase.presentation.feature.listuser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.supabase.R
import com.example.supabase.presentation.navigation.ShowUserDestination
import com.example.supabase.presentation.navigation.UpdateUserDestination
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListUserScreen(
    modifier: Modifier = Modifier,
    viewModel: ListUserViewModel = hiltViewModel(),
    navController: NavController
) {
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.onCreate()
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    SwipeRefresh(state = swipeRefreshState, onRefresh = { viewModel.getUsers() }) {
        Scaffold (
            modifier = modifier.fillMaxSize(),
            topBar = {},
            content = {
                val userList = viewModel.userList.collectAsState(initial = listOf()).value
                if (!userList.isNullOrEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        content = {
                            items(userList) { user ->
                                ListUserItem(user = user,
                                            modifier = modifier,
                                            onClick = {
                                                navController.navigate(ShowUserDestination.createRouteWithParam(user.uuid))
                                            }
                                )
                            }
                        })
                }
                Box (
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ){
                    FloatingActionButton(
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                            navController.navigate(UpdateUserDestination.createRouteWithParam(context.getText(R.string.new_user).toString()))
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        )
    }
}