package com.example.supabase.data.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.supabase.data.repository.UserDataSource
import com.example.supabase.domain.model.User
import com.example.supabase.userDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import userdb.UserEntity

class UserDataSourceImpl(
    db: userDb
) : UserDataSource {

    private val queries = db.userEntityQueries

    override suspend fun getUserByUUID(id: String): User? {
        return withContext(Dispatchers.IO) {
            val userEntity = queries.getUserByUUID(id).executeAsOneOrNull()
            userEntity?.let {
                User(userEntity.uuid, userEntity.first_name, userEntity.last_name, userEntity.email, userEntity.updated_at)
            }
        }
    }

    override suspend fun getUsers(): Flow<List<User>> {
        return withContext(Dispatchers.IO) {
            queries.getUsers().asFlow().map {
                it.executeAsList().map { User(it.uuid, it.first_name, it.last_name, it.email, it.updated_at) }
            }
        }
    }

    override suspend fun getUsersSyncing(time: String): List<User> {
        return withContext(Dispatchers.IO) {
            queries.getUsersSyncing(time).executeAsList().map { User(it.uuid, it.first_name, it.last_name, it.email, it.updated_at) }
        }
    }

    override suspend fun updateUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            val preUser = queries.getUserByUUID(user.uuid).executeAsOneOrNull() ?: return@withContext false
            queries.updateUser(user.first_name, user.last_name, user.email, user.updated_at, user.uuid)
            val postUser = queries.getUserByUUID(user.uuid).executeAsOneOrNull() ?: return@withContext false
            if (preUser.uuid == postUser.uuid && preUser.first_name == postUser.first_name && preUser.last_name == postUser.last_name && preUser.email == postUser.email && preUser.updated_at == postUser.updated_at) {
                return@withContext false
            }
            return@withContext true
        }
    }

    override suspend fun createUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            if (queries.getUserByUUID(user.uuid).executeAsOneOrNull() != null)
                return@withContext false
            queries.createUser(user.uuid, user.first_name, user.last_name, user.email, user.updated_at)
            queries.getUserByUUID(user.uuid).executeAsOneOrNull() ?: return@withContext false
            return@withContext true
        }
    }

    override suspend fun deleteByUUID(userUUID: String): Boolean {
        return withContext(Dispatchers.IO) {
            queries.getUserByUUID(userUUID).executeAsOneOrNull() ?: return@withContext false
            queries.deleteUser(userUUID)
            if (queries.getUserByUUID(userUUID).executeAsOneOrNull() != null) {
                return@withContext false
            }
            return@withContext true
        }
    }
}