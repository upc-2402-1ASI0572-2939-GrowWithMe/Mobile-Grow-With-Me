package com.example.prueba.Notifications.Beans

class Notification {
    var id: Int
    var farmerId: Int
    var title: String
    var message: String

    constructor(
        id: Int,
        farmerId: Int,
        title: String,
        message: String
    ) {
        this.id = id
        this.farmerId = farmerId
        this.title = title
        this.message = message
    }
}
