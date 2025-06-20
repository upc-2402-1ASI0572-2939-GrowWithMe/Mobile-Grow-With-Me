package com.example.prueba.Profile.Interfaces

import retrofit2.Call
import com.example.prueba.Profile.Beans.ConsultantProfile
import com.example.prueba.Profile.Beans.ConsultantProfileSchema
import com.example.prueba.Profile.Beans.FarmerProfile
import com.example.prueba.Profile.Beans.FarmerProfileSchema
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PlaceHolder {

    // === FARMERS ===

    @POST("api/v1/farmers")
    fun createFarmer(@Body farmer: FarmerProfileSchema): Call<FarmerProfile>

    @GET("api/v1/farmers/{id}")
    fun getFarmerById(@Path("id") id: Int): Call<FarmerProfile>

    @GET("api/v1/farmers")
    fun getAllFarmers(): Call<List<FarmerProfile>>


    // === CONSULTANTS ===

    @POST("api/v1/consultants")
    fun createConsultant(@Body consultant: ConsultantProfileSchema): Call<ConsultantProfile>

    @GET("api/v1/consultants/{id}")
    fun getConsultantById(@Path("id") id: Int): Call<ConsultantProfile>

    @GET("api/v1/consultants")
    fun getAllConsultants(): Call<List<ConsultantProfile>>
}