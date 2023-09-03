package com.example.supabase.domain.usecase.impl

import com.example.supabase.data.repository.UserDao
import com.example.supabase.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetUsersUseCaseImpl(
    private val userDao: UserDao
) : GetUsersUseCase {
    override suspend fun execute(input: Unit): GetUsersUseCase.Output =
        withContext(Dispatchers.IO) {
            val result = userDao.getUsers().map { it.map { userModel ->  userModel.toUser() } }
            return@withContext result.let {
                GetUsersUseCase.Output.Success(data = it)
            } ?: GetUsersUseCase.Output.Failure
        }
}