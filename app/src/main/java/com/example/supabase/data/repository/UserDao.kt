package com.example.supabase.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.supabase.data.repository.model.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM Users")
    fun getUsers(): Flow<List<UserModel>>

    @Query("SELECT * FROM Users WHERE uuid = :uuid")
    suspend fun getUserByUUID(uuid: String): UserModel?

    @Query("SELECT * FROM Users WHERE updated_at > :time ORDER BY updated_at DESC")
    suspend fun getUsersSyncing(time: String): List<UserModel>

    @Update
    suspend fun updateUser(user: UserModel): Int

    @Insert
    suspend fun createUser(user: UserModel): Long
}