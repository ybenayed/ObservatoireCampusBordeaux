package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.CampusDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CampusApi {
    @GET("api/campus")
    suspend fun getAllCampus(): List<CampusDto>
}

object RetrofitClient {
    private const val BASE_URL = "http:/10.209.192.195:8080/"

    val api: CampusApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CampusApi::class.java)
    }
}