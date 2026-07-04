package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.CampusDto
import retrofit2.http.GET

interface CampusApi {
    @GET("api/campus")
    suspend fun getAllCampus(): List<CampusDto>
}