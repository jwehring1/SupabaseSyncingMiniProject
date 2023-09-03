package com.example.supabase.domain.model

sealed class Database {
    data object LocalDatabase : Database()
    data object RemoteDatabase : Database()
    data object AllDatabases : Database()
}