package com.example.supabase.di

import android.content.Context
import androidx.room.Room
import com.example.supabase.data.repository.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(
            app,
            UserDatabase::class.java,
            "userDb"
        ).build()

    @Singleton
    @Provides
    fun provideUserDao(db: UserDatabase) = db.userDao()
}