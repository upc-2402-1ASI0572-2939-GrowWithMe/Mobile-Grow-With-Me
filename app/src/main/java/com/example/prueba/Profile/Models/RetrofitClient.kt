package com.example.prueba.Profile.Models

import com.example.prueba.Crops.Models.AuthInterceptor
import com.example.prueba.HttpUri
import com.example.prueba.Profile.Interfaces.PlaceHolder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = HttpUri.url

    fun getClient(token: String? = null): PlaceHolder {
        val builder = OkHttpClient.Builder()

        if (!token.isNullOrEmpty()) {
            builder.addInterceptor(AuthInterceptor(token))
        }

        val client = builder.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlaceHolder::class.java)
    }
}
