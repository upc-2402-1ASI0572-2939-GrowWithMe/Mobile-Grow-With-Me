package com.example.prueba.Consultations.Beans

class Consultation {
    var id: Int = 0
    var farmerId: Int
    var title: String
    var description: String
    var status: String
    var humidity: Int
    var temperature: Float

    constructor(
        id: Int,
        farmerId: Int,
        title: String,
        description: String,
        status: String,
        humidity: Int,
        temperature: Float
    ) {
        this.id = id
        this.farmerId = farmerId
        this.title = title
        this.description = description
        this.status = status
        this.humidity = humidity
        this.temperature = temperature
    }
}
