package com.example.supabase.di

import com.example.supabase.domain.usecase.CreateUserUseCase
import com.example.supabase.domain.usecase.GetShowUserUseCase
import com.example.supabase.domain.usecase.GetUsersSyncingUseCase
import com.example.supabase.domain.usecase.GetUsersUseCase
import com.example.supabase.domain.usecase.UpdateUserUseCase
import com.example.supabase.domain.usecase.impl.CreateUserUseCaseImpl
import com.example.supabase.domain.usecase.impl.GetShowUserUseCaseImpl
import com.example.supabase.domain.usecase.impl.GetUsersSyncingUseCaseImpl
import com.example.supabase.domain.usecase.impl.GetUsersUseCaseImpl
import com.example.supabase.domain.usecase.impl.UpdateUserUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindGetUsersUseCase(impl: GetUsersUseCaseImpl): GetUsersUseCase

    @Binds
    abstract fun bindShowUserUseCase(impl: GetShowUserUseCaseImpl): GetShowUserUseCase

    @Binds
    abstract fun bindCreateUserUseCase(impl: CreateUserUseCaseImpl): CreateUserUseCase

    @Binds
    abstract fun bindUpdateUserUseCase(impl: UpdateUserUseCaseImpl): UpdateUserUseCase

    @Binds
    abstract fun bindGetUsersSyncingUseCase(impl: GetUsersSyncingUseCaseImpl): GetUsersSyncingUseCase

}