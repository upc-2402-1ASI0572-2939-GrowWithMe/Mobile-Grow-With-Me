package com.example.prueba.Profile.Beans

import java.io.Serializable

class FarmerProfile : Serializable {
    var id: Int
    var firstName: String
    var lastName: String
    var email: String
    var phone: String
    var photoUrl: String
    var dni: String

    constructor(
        id: Int,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        photoUrl: String,
        dni: String
    ) {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.phone = phone
        this.photoUrl = photoUrl
        this.dni = dni
    }

    constructor(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        photoUrl: String,
        dni: String
    ) {
        this.id = 0
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.phone = phone
        this.photoUrl = photoUrl
        this.dni = dni
    }
}