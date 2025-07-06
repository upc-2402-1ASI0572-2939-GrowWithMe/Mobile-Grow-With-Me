package com.example.prueba.Activity.Interfaces
import com.example.prueba.Activity.Beans.CropActivity
import com.example.prueba.Activity.Beans.CropActivitySchema
import retrofit2.Call
import retrofit2.http.*

interface PlaceHolder {

    // GET /cropActivities/
    @GET("cropActivities")
    fun getAllActivities(): Call<List<CropActivity>>

    @GET("crop-activities/crops/{cropId}")
    fun getActivitiesByCropId(@Path("cropId") cropId: Int): Call<List<CropActivity>>

    // POST /cropActivities/
    @POST("crop-activities")
    fun createActivity(@Body activity: CropActivitySchema): Call<CropActivity>

    // PUT /cropActivities/{id}
    @PUT("crop-activities/{cropActivityId}")
    fun updateActivity(@Path("cropActivityId") id: Int, @Body activity: CropActivity): Call<CropActivity>

    // DELETE /cropActivities/{id}
    @DELETE("crop-activities/{cropActivityId}")
    fun deleteActivity(@Path("cropActivityId") id: Int): Call<Void>
}