package com.ObservatoireCampus.mobile.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ObservatoireCampus.mobile.network.RetrofitClient
import java.io.File
import com.ObservatoireCampus.mobile.model.CampusDto

class MapRepository(private val context: Context) {

    private val cacheFile = File(context.filesDir, "campus_cache.json")
    private val gson = Gson()

    suspend fun getCampus(): List<CampusDto> {
        return try {
            val data = RetrofitClient.campusApi.getAllCampus()
            saveToCache(data)
            data
        } catch (e: Exception) {
            loadFromCache() ?: throw e
        }
    }

    private fun saveToCache(data: List<CampusDto>) {
        try {
            cacheFile.writeText(gson.toJson(data))
        } catch (e: Exception) {
            // log si besoin, mais ne pas bloquer l'app pour une erreur de cache
        }
    }

    private fun loadFromCache(): List<CampusDto>? {
        if (!cacheFile.exists()) return null
        return try {
            val type = object : TypeToken<List<CampusDto>>() {}.type
            gson.fromJson(cacheFile.readText(), type)
        } catch (e: Exception) {
            null
        }
    }
}