package com.example.prueba.Crops.Interfaces

import com.example.prueba.Crops.Beans.Crop
import retrofit2.Call
import retrofit2.http.*

interface PlaceHolder {

    @GET("crops")
    fun getCrops(): Call<List<Crop>>

    @GET("crops/{id}")
    fun getCropById(@Path("id") id: Long): Call<Crop>

    @GET("crops/farmer/{farmerId}")
    fun getCropsByFarmerId(@Path("farmerId") farmerId: Long): Call<List<Crop>>

    @POST("crops")
    fun createCrop(@Body crop: Crop): Call<Crop>

    @PUT("crops/{id}")
    fun updateCrop(@Path("id") id: Long, @Body crop: Crop): Call<Crop>

    @DELETE("crops/{id}")
    fun deleteCrop(@Path("id") id: Long): Call<Void>
}
