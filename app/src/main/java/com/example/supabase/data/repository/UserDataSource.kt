package com.example.supabase.data.repository

import com.example.supabase.domain.model.User
import kotlinx.coroutines.flow.Flow


interface UserDataSource {
    suspend fun getUserByUUID(id: String): User?

    suspend fun getUsers(): Flow<List<User>>

    suspend fun getUsersSyncing(time: String): List<User>

    suspend fun updateUser(user: User): Boolean

    suspend fun createUser(user: User): Boolean

    suspend fun deleteByUUID(userUUID: String): Boolean

}