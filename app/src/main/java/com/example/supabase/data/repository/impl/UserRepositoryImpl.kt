package com.example.supabase.data.repository.impl

import com.example.supabase.data.repository.UserRepository
import com.example.supabase.domain.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlin.Exception

class UserRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : UserRepository {

    override suspend fun getUsers() : List<User> {
        return try {
            supabaseClient.postgrest["users"]
                .select().decodeList()
        } catch (e: Exception) {
            listOf()
        }
    }

    override suspend fun getUserByUUID(userUUID: String) : User? {
        return try {
            supabaseClient.postgrest["users"].select {
                eq("uuid", userUUID)
            }.decodeSingle()
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun getUsersSyncing(time: String): List<User> {
        return try {
            supabaseClient.postgrest["users"].select {
                gte("updated_at", time)
                order("updated_at",Order.DESCENDING)
            }.decodeList()
        } catch (e: Exception) {
            return listOf()
        }
    }

    override suspend fun updateUser(user: User): Boolean {
        return try {
            supabaseClient.postgrest["users"].insert(user, upsert = true)
            true
        } catch (e: Exception) {
            false
        }

    }

    override suspend fun createUser(user: User): Boolean {
        return try {
            supabaseClient.postgrest["users"].insert(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteByUUID(userUUID: String): Boolean {
        return try {
            supabaseClient.postgrest["users"].delete {
                eq("uuid", userUUID)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}