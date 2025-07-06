package com.example.prueba.StartingPoint.Models

import com.example.prueba.HttpUri
import com.example.prueba.StartingPoint.Interface.PlaceHolder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = HttpUri.url

    val placeHolder: PlaceHolder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PlaceHolder::class.java)
}
