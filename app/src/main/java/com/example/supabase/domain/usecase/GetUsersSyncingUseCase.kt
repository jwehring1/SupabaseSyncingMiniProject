package com.example.supabase.domain.usecase

import com.example.supabase.domain.model.User

interface GetUsersSyncingUseCase : UseCase<GetUsersSyncingUseCase.Input, GetUsersSyncingUseCase.Output> {
    class Input(val time: String)
    sealed class Output {
        class Success(val remoteResult: List<User>, val localResult: List<User>): Output()
        data object Failure : Output()
    }
}