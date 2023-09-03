package com.example.supabase.presentation.feature.showuser

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supabase.domain.model.Database
import com.example.supabase.domain.model.User
import com.example.supabase.domain.usecase.DeleteUserUseCase
import com.example.supabase.domain.usecase.GetShowUserUseCase
import com.example.supabase.presentation.navigation.ShowUserDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ShowUserViewModel(
    private val getShowUserUseCase: GetShowUserUseCase,
    private val getDeleteUserUseCase: DeleteUserUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: Flow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading

    private val _deleteUserResult = MutableStateFlow<DeleteUserUseCase.Output>(DeleteUserUseCase.Output.NotStarted)
    val deleteUserResult: Flow<DeleteUserUseCase.Output> = _deleteUserResult

    private var userUUID: String? = null

    fun onStart() {
        userUUID = savedStateHandle.get<String>(ShowUserDestination.userUUID)
        userUUID?.let {
            getUserByUUID(it)
        }
    }

    fun getUserUUID(): String? {
        return userUUID
    }

    private fun getUserByUUID(userUUID: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = getShowUserUseCase.execute(
                GetShowUserUseCase.Input(
                    uuid = userUUID,
                    Database.LocalDatabase
                )
            )
            when (result) {
                is GetShowUserUseCase.Output.Success -> {
                    _user.emit(result.data)
                    _isLoading.value = false
                }
                is GetShowUserUseCase.Output.Failure -> {
                    _isLoading.value = false
                }
            }
        }
    }

    fun deleteUserByUUID(userUUID: String?) {
        userUUID?.let {
            viewModelScope.launch {
                val result = getDeleteUserUseCase.execute(DeleteUserUseCase.Input(userUUID))
                _deleteUserResult.emit(result)
            }
        }
    }
}