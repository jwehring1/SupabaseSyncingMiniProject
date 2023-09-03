package com.example.supabase.presentation.feature.listuser

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supabase.domain.model.User
import com.example.supabase.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ListUserViewModel(
    private val getUsersUseCase: GetUsersUseCase,
) : ViewModel() {

    private val _userList = MutableStateFlow<List<User>?>(listOf())
    val userList: Flow<List<User>?> = _userList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading

    fun onCreate() {
        getUsers()
    }

    fun getUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = getUsersUseCase.execute(input = Unit)) {
                is GetUsersUseCase.Output.Success -> {
                    result.data.collectLatest { users ->
                        _userList.emit(users)
                        users.forEach {
                            Log.e("Users", it.toString())
                        }
                        _isLoading.value = false
                    }
                }
                is GetUsersUseCase.Output.Failure -> {
                    _isLoading.value = false
                }
            }
        }
    }
}