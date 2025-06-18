package com.example.prueba.Activity.Beans

class CropActivity {
    var id: Int=0
    var cropId: Int
    var activityDate: String
    var description: String

    constructor(id: Int, cropId: Int, activityDate: String, description: String) {
        this.id=id
        this.cropId = cropId
        this.activityDate = activityDate
        this.description = description
    }
}