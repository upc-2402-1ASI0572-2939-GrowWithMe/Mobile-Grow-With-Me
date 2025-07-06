package com.example.prueba.Crops.Beans

import android.app.Activity

class Crop {
    var id: Int
    var farmerId: Int
    var cropActivities: List<Activity>
    var productName: String
    var code: String
    var category: String
    var status: String
    var area: Float
    var location: String
    var cost: Int
    var registrationDate: String

    constructor(
        id: Int,
        farmerId: Int,
        cropActivities: List<Activity>,
        productName: String,
        code: String,
        category: String,
        status: String,
        area: Float,
        location: String,
        cost: Int,
        registrationDate: String
    ) {
        this.id = id
        this.farmerId = farmerId
        this.cropActivities = cropActivities
        this.productName = productName
        this.code = code
        this.category = category
        this.status = status
        this.area = area
        this.location = location
        this.cost = cost
        this.registrationDate = registrationDate
    }
}