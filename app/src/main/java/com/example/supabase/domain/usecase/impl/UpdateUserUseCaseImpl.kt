package com.example.supabase.domain.usecase.impl

import com.example.supabase.data.repository.UserDao
import com.example.supabase.data.repository.UserRepository
import com.example.supabase.domain.model.Database
import com.example.supabase.domain.usecase.UpdateUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class UpdateUserUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val userDao: UserDao
) : UpdateUserUseCase {
    override suspend fun execute(input: UpdateUserUseCase.Input): UpdateUserUseCase.Output {
        return withContext(Dispatchers.IO) {
            if (input.user.updated_at.isEmpty() || input.database == Database.AllDatabases) {
                input.user.updated_at = getCurrentTime()
            }

            val localResult = if (input.database != Database.RemoteDatabase) {
                userDao.updateUser(user = input.user.toUserModel()) > 0
            } else { null }
            val remoteResult = if (input.database != Database.LocalDatabase) {
                userRepository.updateUser(user = input.user)
            } else { null }

            return@withContext if (remoteResult == null || remoteResult) {
                if (localResult == null || localResult) {
                    UpdateUserUseCase.Output.TotalSuccess
                } else {
                    UpdateUserUseCase.Output.LocalDatabaseFailure
                }
            } else {
                if (localResult == null || localResult) {
                    UpdateUserUseCase.Output.RemoteDatabaseFailure
                } else {
                    UpdateUserUseCase.Output.TotalFailure
                }
            }
        }
    }

    private fun getCurrentTime(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx")
        return Instant.now().atOffset(ZoneOffset.UTC).format(formatter)
    }
}