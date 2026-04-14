package com.example.myapplication.data.model

enum class UserRole {
    ADMIN, TECHNICIAN, MANAGER
}

data class User(
    val id: String,
    val name: String,
    val role: UserRole,
    val email: String
)
