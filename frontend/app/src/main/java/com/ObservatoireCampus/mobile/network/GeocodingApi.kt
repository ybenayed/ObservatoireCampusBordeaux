package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface Retrofit vers notre backend Spring (endpoint /api/search).
 * Le backend interroge lui-même Nominatim côté serveur ; l'app Android
 * n'appelle jamais Nominatim directement.
 */
interface GeocodingApi {

    @GET("api/search")
    suspend fun search(@Query("q") query: String): List<SearchResultDto>
}