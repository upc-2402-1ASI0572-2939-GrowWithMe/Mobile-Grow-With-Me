package com.example.prueba.Crops.Beans

import android.app.Activity
import java.util.*

data class CropSchema(
    var farmerId: Int,
    var cropActivities: List<Activity>,
    var productName: String,
    var code: String,
    var category: String,
    var status: String,
    var area: Float,
    var location: String,
    var cost: Int,
    var registrationDate: String
)
