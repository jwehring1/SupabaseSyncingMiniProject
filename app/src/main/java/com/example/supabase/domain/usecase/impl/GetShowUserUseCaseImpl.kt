package com.example.supabase.domain.usecase.impl

import com.example.supabase.data.repository.UserDao
import com.example.supabase.data.repository.UserRepository
import com.example.supabase.domain.model.Database
import com.example.supabase.domain.model.User
import com.example.supabase.domain.usecase.GetShowUserUseCase
import javax.inject.Inject

class GetShowUserUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val userDao: UserDao,
) : GetShowUserUseCase {
    override suspend fun execute(input: GetShowUserUseCase.Input): GetShowUserUseCase.Output {
        val result = when(input.database) {
            Database.LocalDatabase -> {
                userDao.getUserByUUID(input.uuid)?.toUser()
            }
            Database.RemoteDatabase -> {
                userRepository.getUserByUUID(input.uuid)
            }
            else -> {
                null
            }
        }

        if (result != null) {
            return GetShowUserUseCase.Output.Success(
                data = User(
                    uuid = result.uuid,
                    first_name = result.first_name,
                    last_name = result.last_name,
                    email = result.email,
                    updated_at = result.updated_at
                )
            )
        }
        return GetShowUserUseCase.Output.Failure
    }
}