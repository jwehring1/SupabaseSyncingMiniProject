package com.example.supabase.domain.usecase

import com.example.supabase.domain.model.Database
import com.example.supabase.domain.model.User

interface GetShowUserUseCase : UseCase<GetShowUserUseCase.Input, GetShowUserUseCase.Output> {
    class Input(val uuid: String, val database: Database)
    sealed class Output {
        class Success(val data: User): Output()
        data object Failure : Output()
    }
}