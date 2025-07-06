package com.example.prueba.StartingPoint.Beans

data class UserAuthenticated(
    val id: Int,
    val email: String,
    val token: String,
    val message: String,
    val roles: List<String>
)
