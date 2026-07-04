package com.ObservatoireCampus.mobile.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.209.192.195:8080/"  // typo corrigee : http:// pas http:/

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val campusApi: CampusApi by lazy { retrofit.create(CampusApi::class.java) }
    val parkingApi: ParkingApi by lazy { retrofit.create(ParkingApi::class.java) }
}