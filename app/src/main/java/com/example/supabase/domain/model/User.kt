package com.example.supabase.domain.model

import com.example.supabase.data.repository.model.UserModel
import kotlinx.serialization.Serializable

@Serializable
data class User (
    var uuid: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val email: String = "",
    var updated_at: String = ""
) {
    fun toUserModel(): UserModel {
        return UserModel(uuid, first_name, last_name, email, updated_at)
    }
}