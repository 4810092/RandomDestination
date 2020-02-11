package com.example.vezubrnavkotlin.api

import com.gka.codesample.randomdestination.api.ResponseRoute
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {
    @GET("/maps/api/directions/json")
    suspend fun getRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String
    ): Response<ResponseRoute>

}