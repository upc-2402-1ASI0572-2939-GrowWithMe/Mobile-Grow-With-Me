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
    @GET("users/farmers")
    fun getAllFarmers(): Call<List<FarmerProfile>>


    // === CONSULTANTS ===
    @GET("users/consultants")
    fun getAllConsultants(): Call<List<ConsultantProfile>>
}
