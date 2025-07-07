package com.example.prueba.Devices.Interfaces

import com.example.prueba.Devices.Beans.Device
import com.example.prueba.Devices.Beans.DeviceSchema
import retrofit2.Call
import retrofit2.http.*

interface PlaceHolder {

    @POST("devices")
    fun createDevice(
        @Body resource: DeviceSchema
    ): Call<Device>

    @GET("devices/farmer")
    fun getDevicesByFarmerId(): Call<List<Device>>

    @GET("devices/sensor-data/{deviceId}")
    fun getSensorData(@Path("deviceId") deviceId: Long): Call<Device>
}
