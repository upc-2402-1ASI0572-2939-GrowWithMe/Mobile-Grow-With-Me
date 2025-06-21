package com.example.prueba.Profile.Interfaces

import retrofit2.Call
import com.example.prueba.Profile.Beans.ConsultantProfile
import com.example.prueba.Profile.Beans.ConsultantProfileSchema
import com.example.prueba.Profile.Beans.FarmerProfile
import com.example.prueba.Profile.Beans.FarmerProfileSchema
import com.google.gson.JsonObject
import retrofit2.http.*

interface PlaceHolder {

    // === FARMERS ===

    @POST("farmers")
    fun createFarmer(@Body farmer: FarmerProfileSchema): Call<FarmerProfile>

    @GET("farmers/{id}")
    fun getFarmerById(@Path("id") id: Int): Call<FarmerProfile>

    @PUT("farmers/{id}")
    fun updateFarmer(@Path("id") id: Int, @Body body: JsonObject): Call<FarmerProfile>
    @GET("farmers")
    fun getAllFarmers(): Call<List<FarmerProfile>>


    // === CONSULTANTS ===

    @POST("consultants")
    fun createConsultant(@Body consultant: ConsultantProfileSchema): Call<ConsultantProfile>

    @GET("consultants/{id}")
    fun getConsultantById(@Path("id") id: Int): Call<ConsultantProfile>

    @PUT("consultants/{id}")
    fun updateConsultant(@Path("id") id: Int, @Body body: JsonObject): Call<ConsultantProfile>
    @GET("consultants")
    fun getAllConsultants(): Call<List<ConsultantProfile>>
}
