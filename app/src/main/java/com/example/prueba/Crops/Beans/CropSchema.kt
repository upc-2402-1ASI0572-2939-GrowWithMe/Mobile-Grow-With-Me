package com.example.prueba.Crops.Beans

data class CropSchema(
    var code: String,
    var productName: String,
    var category: String,
    var area: Int,
    var location: String,
    var status: String,
    var cost: Int,
    var profitReturn: Int,
    var profileId: Int
)
