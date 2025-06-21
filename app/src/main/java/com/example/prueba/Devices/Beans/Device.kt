package com.example.prueba.Devices.Beans

class Device {
    var id: Int = 0
    var name: String
    var token: String
    var deviceType: String
    var status: String

    constructor(
        id: Int,
        name: String,
        token: String,
        deviceType: String,
        status: String
    ) {
        this.id = id
        this.name = name
        this.token = token
        this.deviceType = deviceType
        this.status = status
    }
}
