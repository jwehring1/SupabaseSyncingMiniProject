package com.example.supabase.domain.usecase.impl

import com.example.supabase.data.repository.UserDataSource
import com.example.supabase.data.repository.UserRepository
import com.example.supabase.domain.usecase.DeleteUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteUserUseCaseImpl(
    private val userRepository: UserRepository,
    private val userDao: UserDataSource
) : DeleteUserUseCase {
    override suspend fun execute(input: DeleteUserUseCase.Input): DeleteUserUseCase.Output {
        return withContext(Dispatchers.IO) {

            val localResult = userDao.deleteByUUID(input.userUUID)
            val remoteResult = userRepository.deleteByUUID(input.userUUID)

            return@withContext if (remoteResult) {
                if (localResult) {
                    DeleteUserUseCase.Output.TotalSuccess
                } else {
                    DeleteUserUseCase.Output.LocalDatabaseFailure
                }
            } else {
                if (localResult) {
                    DeleteUserUseCase.Output.RemoteDatabaseFailure
                } else {
                    DeleteUserUseCase.Output.TotalFailure
                }
            }
        }
    }
}