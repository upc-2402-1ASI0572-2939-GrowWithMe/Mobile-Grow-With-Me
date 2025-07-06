package com.example.prueba.StartingPoint.Beans

data class UserSchema(
    val email: String,
    val password: String,
    val roles: List<String>,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val photoUrl: String,
    val dni: String
)
