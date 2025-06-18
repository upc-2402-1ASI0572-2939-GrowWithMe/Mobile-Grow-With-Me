package com.example.prueba.Crops.Models

import com.example.prueba.Crops.Interfaces.PlaceHolder
import com.example.prueba.HttpUri
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = HttpUri.url

    fun getClient(token: String): PlaceHolder {
        /*
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()
*/
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            //.client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlaceHolder::class.java)
    }
}