package com.example.supabase.data.repository.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.supabase.domain.model.User

@Entity("Users")
data class UserModel (
    @PrimaryKey(autoGenerate = false)
    val uuid: String = "",
    val first_name: String,
    val last_name: String,
    val email: String,
    val updated_at: String = ""
) {
    fun toUser(): User {
        return User(uuid, first_name, last_name, email, updated_at)
    }
}