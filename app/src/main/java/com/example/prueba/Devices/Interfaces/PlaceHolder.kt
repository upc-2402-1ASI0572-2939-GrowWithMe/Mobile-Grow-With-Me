package com.example.prueba.Devices.Interfaces

import com.example.prueba.Devices.Beans.Device
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface PlaceHolder {

    @POST("devices")
    fun createDevice(@Body device: JsonObject): Call<Device>

    @GET("devices")
    fun getDevices(): Call<List<Device>>

    @GET("devices")
    fun getDevicesByFarmerId(@Query("farmerId") farmerId: String): Call<List<Device>>
    @PUT("devices/{id}")
    fun updateDeviceStatus(@Path("id") deviceId: Int, @Body statusUpdate: JsonObject): Call<Device>
}
