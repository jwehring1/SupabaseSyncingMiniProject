package com.example.supabase.presentation.feature.updateuser

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supabase.domain.model.Database
import com.example.supabase.domain.model.User
import com.example.supabase.domain.usecase.CreateUserUseCase
import com.example.supabase.domain.usecase.GetShowUserUseCase
import com.example.supabase.domain.usecase.UpdateUserUseCase
import com.example.supabase.presentation.navigation.ShowUserDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UpdateUserViewModel(
    private val getShowUserUseCase: GetShowUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: Flow<User?> = _user

    private val _firstName = MutableStateFlow("")
    val firstName: Flow<String> = _firstName

    private val _lastName = MutableStateFlow("")
    val lastName: Flow<String> = _lastName

    private val _email = MutableStateFlow("")
    val email: Flow<String> = _email

    private val _createUserResult = MutableStateFlow<CreateUserUseCase.Output>(CreateUserUseCase.Output.NotStarted)
    val createUserResult: Flow<CreateUserUseCase.Output> = _createUserResult

    private val _updateUserResult = MutableStateFlow<UpdateUserUseCase.Output>(UpdateUserUseCase.Output.NotStarted)
    val updateUserResult: Flow<UpdateUserUseCase.Output> = _updateUserResult

    init {
        val userUUID = savedStateHandle.get<String>(ShowUserDestination.userUUID)
        userUUID?.let {
            getUserByUUID(it)
        }
    }

    fun onFirstNameChange(firstName: String) {
        _firstName.value = firstName
    }

    fun onLastNameChange(lastName: String) {
        _lastName.value = lastName
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun createUser(user: User) {
        viewModelScope.launch {
            val result = createUserUseCase.execute(CreateUserUseCase.Input(user = user, Database.AllDatabases))
            _createUserResult.emit(result)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            val result = updateUserUseCase.execute(UpdateUserUseCase.Input(user = user, Database.AllDatabases))
            _updateUserResult.emit(result)
        }
    }

    private fun getUserByUUID(userUUID: String) {
        viewModelScope.launch {
            val result = getShowUserUseCase.execute(
                GetShowUserUseCase.Input(
                    uuid = userUUID,
                    Database.LocalDatabase
                )
            )
            when (result) {
                is GetShowUserUseCase.Output.Success -> {
                    _user.emit(result.data)
                    _firstName.emit(result.data.first_name)
                    _lastName.emit(result.data.last_name)
                    _email.emit(result.data.email)
                }
                is GetShowUserUseCase.Output.Failure -> {

                }
            }
        }
    }

}