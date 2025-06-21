package com.example.prueba.Consultations.Interfaces

import com.example.prueba.Consultations.Beans.Consultation
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface PlaceHolder {

    @GET("consultations")
    fun getConsultationsByFarmerId(@Query("farmerId") farmerId: Int): Call<List<Consultation>>

    @POST("consultations")
    fun createConsultation(@Body consultation: JsonObject): Call<Consultation>
}
