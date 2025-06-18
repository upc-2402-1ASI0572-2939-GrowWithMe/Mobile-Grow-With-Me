package com.example.prueba.Crops.Beans

class Crop {
    var id: Int=0
    var code: String
    var productName: String
    var category: String
    var area: Int
    var location: String
    var status: String
    var cost: Int
    var profitReturn: Int
    var profileId: Int

    constructor(
        id: Int,
        code: String,
        productName: String,
        category: String,
        area: Int,
        location: String,
        status: String,
        cost: Int,
        profitReturn: Int,
        profileId: Int
    ) {
        this.id = id
        this.code = code
        this.productName = productName
        this.category = category
        this.area = area
        this.location = location
        this.status = status
        this.cost = cost
        this.profitReturn = profitReturn
        this.profileId = profileId
    }
}
