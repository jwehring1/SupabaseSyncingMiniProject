package com.example.supabase.domain.usecase

interface DeleteUserUseCase : UseCase<DeleteUserUseCase.Input, DeleteUserUseCase.Output> {
    class Input(val userUUID: String)
    sealed class Output {
        data object NotStarted : Output()
        data object TotalSuccess : Output()
        data object LocalDatabaseFailure : Output()
        data object RemoteDatabaseFailure : Output()
        data object TotalFailure : Output()
    }
}