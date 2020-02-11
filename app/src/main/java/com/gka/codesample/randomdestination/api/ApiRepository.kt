package com.gka.codesample.randomdestination.api

import com.example.vezubrnavkotlin.api.ApiClient
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://maps.googleapis.com/"

object ApiRepository {
    private val webservice by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(ApiClient::class.java)
    }


    suspend fun getRoute(
        origin: String,
        destination: String,
        key: String
    ) = webservice.getRoute(origin, destination, key)
}
