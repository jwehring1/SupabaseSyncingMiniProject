package com.example.supabase.domain.usecase.impl

import com.example.supabase.data.repository.UserDao
import com.example.supabase.data.repository.UserRepository
import com.example.supabase.domain.model.User
import com.example.supabase.domain.usecase.GetUsersSyncingUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUsersSyncingUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val userDao: UserDao
) : GetUsersSyncingUseCase {
    override suspend fun execute(input: GetUsersSyncingUseCase.Input): GetUsersSyncingUseCase.Output =
        withContext(Dispatchers.IO) {
            val repositoryResult = userRepository.getUsersSyncing(input.time)
            val databaseResult = userDao.getUsersSyncing(input.time)
            return@withContext GetUsersSyncingUseCase.Output.Success(
                remoteResult = repositoryResult.map {
                User(
                    uuid = it.uuid,
                    first_name = it.first_name,
                    last_name = it.last_name,
                    email = it.email,
                    updated_at = it.updated_at
                )
            }, localResult = databaseResult.map {
                User(
                    uuid = it.uuid,
                    first_name = it.first_name,
                    last_name = it.last_name,
                    email = it.email,
                    updated_at = it.updated_at
                )
            })
        }
}