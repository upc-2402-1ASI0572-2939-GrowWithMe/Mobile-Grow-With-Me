package com.example.prueba.StartingPoint.Interface

import com.example.prueba.StartingPoint.Beans.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PlaceHolder {

    @POST("authentication/sign-up")
    fun signUp(@Body userSchema: UserSchema): Call<User>

    @POST("authentication/sign-in")
    fun signIn(@Body userAuth: UserAuth): Call<UserAuthenticated>
}
