import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.supabase.domain.model.User
import com.example.supabase.presentation.feature.showuser.ShowUserViewModel
import com.example.supabase.presentation.navigation.UpdateUserDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowUserScreen(
    modifier: Modifier = Modifier,
    viewModel: ShowUserViewModel = hiltViewModel(),
    navController: NavController
) {
    val user = viewModel.user.collectAsState(initial = User())
    val isLoading by viewModel.isLoading.collectAsState(initial = false)

    val lifecycleOwner = LocalLifecycleOwner.current

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.onStart()
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Display User") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { if(user.value != null) { navController.navigate(UpdateUserDestination.createRouteWithParam(viewModel.getUserUUID()))} else { navController.navigate(UpdateUserDestination.route)} }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit User")
                    }
                }
            )
        }
    ){ values ->
        if(isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(values).padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .padding(horizontal = 16.dp)
            ) {
                ProfileHeader(firstName = user.value?.first_name ?: "", lastName = user.value?.last_name ?: "")
                Spacer(modifier = Modifier.height(16.dp))
                EmailSection(email = user.value?.email ?: "")
            }
        }
    }

}

@Composable
fun ProfileHeader(firstName: String, lastName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Avatar(name = "$firstName $lastName")
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "$firstName $lastName", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun Avatar(name: String) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = name.take(2).uppercase(),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
    }
}

@Composable
fun EmailSection(email: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Contact Information", style = MaterialTheme.typography.displaySmall)
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = email,
            onValueChange = {},
            textStyle = MaterialTheme.typography.bodySmall,
            singleLine = true,
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(12.dp)
        )
    }
}

@Preview
@Composable
fun UserProfileScreenPreview() {
    val firstName = "John"
    val lastName = "Doe"
    val email = "john.doe@example.com"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProfileHeader(firstName = firstName, lastName = lastName)
        Spacer(modifier = Modifier.height(16.dp))
        EmailSection(email = email)
    }
}