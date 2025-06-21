package com.example.prueba.Consultations.Beans

data class ConsultationSchema(
    var farmerId: Int,
    var title: String,
    var description: String,
    var status: String,
    var humidity: Int,
    var temperature: Float
)
