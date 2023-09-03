package com.example.supabase.domain.usecase.impl

import com.example.supabase.data.repository.UserDao
import com.example.supabase.data.repository.UserRepository
import com.example.supabase.domain.model.Database
import com.example.supabase.domain.usecase.CreateUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

class CreateUserUseCaseImpl(
    private val userRepository: UserRepository,
    private val userDao: UserDao
) : CreateUserUseCase {
    override suspend fun execute(input: CreateUserUseCase.Input): CreateUserUseCase.Output {
        return withContext(Dispatchers.IO) {
            if (input.user.updated_at.isNullOrEmpty()) {
                input.user.updated_at = getCurrentTime()
            }
            if (input.user.uuid.isNullOrEmpty()) {
                input.user.uuid = UUID.randomUUID().toString()
            }

            val localResult = if (input.database != Database.RemoteDatabase) {
                userDao.createUser(user = input.user.toUserModel()) > 0
            } else { null }
            val remoteResult = if (input.database != Database.LocalDatabase) {
                userRepository.createUser(user = input.user)
            } else { null }

            return@withContext if (remoteResult == null || remoteResult) {
                if (localResult == null || localResult) {
                    CreateUserUseCase.Output.TotalSuccess
                } else {
                    CreateUserUseCase.Output.LocalDatabaseFailure
                }
            } else {
                if (localResult == null || localResult) {
                    CreateUserUseCase.Output.RemoteDatabaseFailure
                } else {
                    CreateUserUseCase.Output.TotalFailure
                }
            }
        }
    }

    private fun getCurrentTime(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx")
        return Instant.now().atOffset(ZoneOffset.UTC).format(formatter)
    }
}