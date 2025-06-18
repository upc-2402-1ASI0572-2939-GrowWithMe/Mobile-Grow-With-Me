package com.example.prueba.Activity.Interfaces
import com.example.prueba.Activity.Beans.CropActivity
import com.example.prueba.Activity.Beans.CropActivitySchema
import retrofit2.Call
import retrofit2.http.*

interface PlaceHolder {

    // GET /cropActivities/
    @GET("cropActivities")
    fun getAllActivities(): Call<List<CropActivity>>

    // GET /cropActivities?cropId={cropId}
    @GET("cropActivities")
    fun getActivitiesByCropId(@Query("cropId") cropId: Int): Call<List<CropActivity>>

    // POST /cropActivities/
    @POST("cropActivities")
    fun createActivity(@Body activity: CropActivitySchema): Call<CropActivity>

    // PUT /cropActivities/{id}
    @PUT("cropActivities/{id}")
    fun updateActivity(@Path("id") id: Int, @Body activity: CropActivity): Call<CropActivity>

    // DELETE /cropActivities/{id}
    @DELETE("cropActivities/{id}")
    fun deleteActivity(@Path("id") id: Int): Call<Void>
}