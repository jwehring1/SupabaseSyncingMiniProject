package com.example.supabase.presentation.feature.showuser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.supabase.R
import com.example.supabase.domain.model.User
import com.example.supabase.domain.usecase.CreateUserUseCase
import com.example.supabase.domain.usecase.UpdateUserUseCase
import com.example.supabase.presentation.feature.updateuser.UpdateUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateUserScreen(
    modifier: Modifier = Modifier,
    viewModel: UpdateUserViewModel = hiltViewModel(),
    navController: NavController,
    userUUID: String?
) {
    val context = LocalContext.current
    val user = viewModel.user.collectAsStateWithLifecycle(initialValue = User())
    val firstName = viewModel.firstName.collectAsStateWithLifecycle(initialValue = "")
    val lastName = viewModel.lastName.collectAsStateWithLifecycle(initialValue = "")
    val email = viewModel.email.collectAsStateWithLifecycle(initialValue = "")
    val createUserResult = viewModel.createUserResult.collectAsStateWithLifecycle(initialValue = CreateUserUseCase.Output.NotStarted)
    val updateUserResult = viewModel.updateUserResult.collectAsStateWithLifecycle(initialValue = UpdateUserUseCase.Output.NotStarted)
    var showDialog by remember { mutableStateOf(false) }
    val isEmailValid = remember { mutableStateOf(true) }
    val isFirstNameValid = remember { mutableStateOf(true) }
    val isLastNameValid = remember { mutableStateOf(true) }
    val changeFirstNameLambda = remember<(String) -> Unit> {
        {
            viewModel.onFirstNameChange(it)
            isFirstNameValid.value = isValidName(it)
        }
    }
    val changeLastNameLambda = remember<(String) -> Unit> {
        {
            viewModel.onLastNameChange(it)
            isLastNameValid.value = isValidName(it)
        }
    }
    val changeEmailLambda = remember<(String) -> Unit> {
        {
            viewModel.onEmailChange(it)
            isEmailValid.value = isValidEmail(it)
        }
    }
    val submitUserLambda = remember <() -> Unit>{
        {
            user.value?.let {
                viewModel.updateUser(User(user.value!!.uuid,first_name = firstName.value, last_name = lastName.value, email = email.value, updated_at = user.value!!.updated_at))
            } ?: run {
                viewModel.createUser(User(first_name = firstName.value, last_name = lastName.value, email = email.value))
            }
        }
    }
    val submitEnabled = remember {
        derivedStateOf { isEmailValid.value && isFirstNameValid.value && isLastNameValid.value && firstName.value.isNotEmpty() && lastName.value.isNotEmpty() && email.value.isNotEmpty() }
    }
    when(createUserResult.value) {
        CreateUserUseCase.Output.NotStarted -> {}
        else -> {
            showDialog = true
        }
    }
    when(updateUserResult.value) {
        UpdateUserUseCase.Output.NotStarted -> {}
        else -> {
            showDialog = true
        }
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = if(userUUID.isNullOrEmpty() || userUUID == context.getString(R.string.new_user)){"Add User"}else{"Edit User"}) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ){ values ->
        InsertionStatusDialog(
            showDialog = showDialog,
            createUserUseCase = createUserResult.value,
            updateUserUseCase = updateUserResult.value,
            onCloseDialog = {
                showDialog = false
                navController.popBackStack()
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = firstName.value,
                onValueChange = changeFirstNameLambda,
                label = { Text(text = "First Name") },
                isError = !isFirstNameValid.value
            )

            AnimatedVisibility (!isFirstNameValid.value) {
                Text(
                    text = "Please enter a valid first name",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = lastName.value,
                onValueChange = changeLastNameLambda,
                label = { Text(text = "Last Name") },
                isError = !isLastNameValid.value
            )

            AnimatedVisibility (!isLastNameValid.value) {
                Text(
                    text = "Please enter a valid last name",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email.value,
                onValueChange = changeEmailLambda,
                label = { Text(text = "Email") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                isError = !isEmailValid.value
            )

            AnimatedVisibility (!isEmailValid.value) {
                Text(
                    text = "Please enter a valid email address",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    enabled = submitEnabled.value,
                    onClick = submitUserLambda,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = "Submit")
                }
            }
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    return email.matches(Regex(pattern))
}

private fun isValidName(name: String): Boolean {
    val pattern = "^[a-zA-Z\\s-]+$"
    return name.matches(Regex(pattern))
}

@Composable
fun InsertionStatusDialog(
    showDialog: Boolean,
    createUserUseCase: CreateUserUseCase.Output,
    updateUserUseCase: UpdateUserUseCase.Output,
    onCloseDialog: () -> Unit
) {
    if (showDialog) {

        var title: String
        var text: String
        var successful = false
        when(createUserUseCase) {
            CreateUserUseCase.Output.TotalSuccess -> {
                title = "User Created"
                text = "User created successfully"
                successful = true
            }
            CreateUserUseCase.Output.LocalDatabaseFailure -> {
                title = "Partial Success"
                text = "User created remotely but not locally"
            }
            CreateUserUseCase.Output.RemoteDatabaseFailure -> {
                title = "Partial Success"
                text = "User created locally but not remotely"
            }
            CreateUserUseCase.Output.TotalFailure -> {
                title = "Failure"
                text = "User not created"
            }
            CreateUserUseCase.Output.NotStarted -> {
                title = ""
                text = ""
            }
        }
        when(updateUserUseCase) {
            UpdateUserUseCase.Output.TotalSuccess -> {
                title = "User Updated"
                text = "User updated successfully"
                successful = true
            }
            UpdateUserUseCase.Output.LocalDatabaseFailure -> {
                title = "Partial Success"
                text = "User updated remotely but not locally"
            }
            UpdateUserUseCase.Output.RemoteDatabaseFailure -> {
                title = "Partial Success"
                text = "User updated locally but not remotely"
            }
            UpdateUserUseCase.Output.TotalFailure -> {
                title = "Failure"
                text = "User not updated"
            }
            UpdateUserUseCase.Output.NotStarted -> {}
        }
        AlertDialog(
            onDismissRequest = { onCloseDialog() },
            title = {
                Text(
                    text = title,
                    color = if (successful) Color.Green else Color.Red
                )
            },
            text = {
                Text(
                    text = text
                )
            },
            confirmButton = {
                Button(onClick = { onCloseDialog() }) {
                    Text("Close")
                }
            }
        )
    }
}
