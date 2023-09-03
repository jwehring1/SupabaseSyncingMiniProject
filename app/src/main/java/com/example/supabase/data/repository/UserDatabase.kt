package com.example.supabase.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.supabase.data.repository.model.UserModel

@Database(entities = [UserModel::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}