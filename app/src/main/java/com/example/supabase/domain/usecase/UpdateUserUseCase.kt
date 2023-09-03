package com.example.supabase.domain.usecase

import com.example.supabase.domain.model.Database
import com.example.supabase.domain.model.User

interface UpdateUserUseCase : UseCase<UpdateUserUseCase.Input, UpdateUserUseCase.Output> {
    class Input(val user: User, val database: Database)

    sealed class Output() {
        data object NotStarted : Output()
        data object TotalSuccess : Output()
        data object LocalDatabaseFailure : Output()
        data object RemoteDatabaseFailure : Output()
        data object TotalFailure : Output()
    }
}