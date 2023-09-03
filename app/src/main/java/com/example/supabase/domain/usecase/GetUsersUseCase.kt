package com.example.supabase.domain.usecase

import com.example.supabase.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GetUsersUseCase : UseCase<Unit, GetUsersUseCase.Output> {
    sealed class Output {
        class Success(val data: Flow<List<User>>): Output()
        data object Failure : Output()
    }
}