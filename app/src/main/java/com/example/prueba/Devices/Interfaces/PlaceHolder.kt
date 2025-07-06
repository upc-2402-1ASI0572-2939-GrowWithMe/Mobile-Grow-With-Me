package com.example.prueba.Devices.Interfaces

import com.example.prueba.Devices.Beans.Device
import retrofit2.Call
import retrofit2.http.*

interface PlaceHolder {

    @POST("devices")
    fun createDevice(
        @Query("cropId") cropId: Long,
        @Query("farmerId") farmerId: Int,
        @Query("name") name: String,
        @Query("isActive") isActive: Boolean
    ): Call<Device>

    @GET("devices/farmer")
    fun getDevicesByFarmerId(@Query("farmerId") farmerId: String): Call<List<Device>>

    @GET("devices/sensor-data/{deviceId}")
    fun getSensorData(@Path("deviceId") deviceId: Long): Call<Device>
}
