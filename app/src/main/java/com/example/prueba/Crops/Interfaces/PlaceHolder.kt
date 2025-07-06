package com.example.prueba.Crops.Interfaces

import com.example.prueba.Crops.Beans.Crop
import com.example.prueba.Crops.Beans.CropSchema
import com.example.prueba.Crops.Beans.CropSchema2
import retrofit2.Call
import retrofit2.http.*

interface PlaceHolder {

    @GET("crops")
    fun getCrops(): Call<List<Crop>>

    @GET("crops/farmer/{farmerId}")
    fun getCropsByFarmerId(@Path("farmerId") farmerId: Long): Call<List<Crop>>

    @POST("crops")
    fun createCrop(@Body crop: CropSchema): Call<Crop>

    @PUT("crops/{id}")
    fun updateCrop(@Path("id") id: Long, @Body crop: CropSchema2): Call<Crop>

    @DELETE("crops/{id}")
    fun deleteCrop(@Path("id") id: Long): Call<Void>
}
