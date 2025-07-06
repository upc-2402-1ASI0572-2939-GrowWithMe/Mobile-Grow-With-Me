package com.example.prueba.Devices.Beans

data class DeviceSchema(
    var cropId: Int,
    var farmerId: Int,
    var name: String,
    var temperatureList: List<Double> = emptyList(),
    var humidityList: List<Double> = emptyList(),
    var isActive: Boolean = false
)
