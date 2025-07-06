package com.example.prueba.Notifications.Interfaces

import com.example.prueba.Notifications.Beans.Notification
import retrofit2.Call
import retrofit2.http.GET

interface PlaceHolder {
    @GET("notifications/farmer")
    fun getNotifications(): Call<List<Notification>>
}

