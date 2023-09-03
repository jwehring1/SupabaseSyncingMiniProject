package com.example.supabase.data.repository

import com.example.supabase.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun getUserByUUID(userUUID: String): User?
    suspend fun getUsersSyncing(time: String): List<User>
    suspend fun deleteByUUID(userUUID: String): Boolean
    suspend fun updateUser(user: User): Boolean
    suspend fun createUser(user: User): Boolean
}