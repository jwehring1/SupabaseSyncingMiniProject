package com.example.supabase.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User (
    var uuid: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val email: String = "",
    var updated_at: String = ""
)