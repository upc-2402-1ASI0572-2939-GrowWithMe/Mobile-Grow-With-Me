package com.example.prueba.Devices.Beans

class Device {
    var id: Int
    var cropId: Int
    var farmerId: Int
    var name: String
    var temperatureList: List<Double>
    var humidityList: List<Double>
    var isActive: Boolean

    constructor(
        id: Int,
        cropId: Int,
        farmerId: Int,
        name: String,
        temperatureList: List<Double> = emptyList(),
        humidityList: List<Double> = emptyList(),
        isActive: Boolean = false
    ) {
        this.id = id
        this.cropId = cropId
        this.farmerId = farmerId
        this.name = name
        this.temperatureList = temperatureList
        this.humidityList = humidityList
        this.isActive = isActive
    }
}
