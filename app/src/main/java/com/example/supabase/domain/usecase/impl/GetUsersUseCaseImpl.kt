package com.example.supabase.domain.usecase.impl

import com.example.supabase.data.repository.UserDataSource
import com.example.supabase.domain.model.User
import com.example.supabase.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetUsersUseCaseImpl(
    private val userDao: UserDataSource
) : GetUsersUseCase {
    override suspend fun execute(input: Unit): GetUsersUseCase.Output =
        withContext(Dispatchers.IO) {
            val result = userDao.getUsers()
            return@withContext result.let {
                GetUsersUseCase.Output.Success(data = it)
            } ?: GetUsersUseCase.Output.Failure
        }
}