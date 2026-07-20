package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

/**
 * Couche repository standard (même rôle que ParkingRepository, MapRepository...) :
 * fait l'intermédiaire entre les ViewModels et Retrofit.
 */
class SearchRepository {

    suspend fun searchPlaces(query: String): List<SearchResultDto> {
        return RetrofitClient.geocodingApi.search(query)
    }
}