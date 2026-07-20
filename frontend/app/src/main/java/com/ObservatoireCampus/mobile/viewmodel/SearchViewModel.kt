package com.ObservatoireCampus.mobile.viewmodel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.search.SearchResultDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class SearchViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _suggestions = MutableStateFlow<List<SearchResultDto>>(emptyList())
    val suggestions: StateFlow<List<SearchResultDto>> = _suggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery

        // Annuler la recherche précédente si l'utilisateur tape vite (Debounce)
        searchJob?.cancel()

        if (newQuery.isBlank() || newQuery.length < 3) {
            _suggestions.value = emptyList()
            _isLoading.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isLoading.value = true
            delay(500) // Attendre 500ms sans saisie avant de lancer la requête

            _suggestions.value = try {
                fetchSuggestionsFromApi(newQuery)
            } catch (e: Exception) {
                emptyList()
            }

            _isLoading.value = false
        }
    }

    /**
     * Appelle l'API de géocodage Nominatim (OpenStreetMap) pour trouver des lieux
     * correspondant à la requête, avec un biais géographique autour de Bordeaux/Pessac.
     */
    private suspend fun fetchSuggestionsFromApi(query: String): List<SearchResultDto> =
        withContext(Dispatchers.IO) {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")

            val urlString = "https://nominatim.openstreetmap.org/search" +
                    "?q=$encodedQuery" +
                    "&format=json" +
                    "&limit=6" +                      // Liste plus courte, plus lisible
                    "&addressdetails=0" +
                    "&accept-language=fr" +
                    "&viewbox=-0.75,44.90,-0.45,44.70" +
                    "&bounded=0"

            val connection = URL(urlString).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "ObservatoireCampusApp/1.0 (contact@votre-domaine.fr)")
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            try {
                if (connection.responseCode != 200) {
                    return@withContext emptyList()
                }

                val body = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(body)
                val results = mutableListOf<SearchResultDto>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val displayName = obj.optString("display_name")
                    val lat = obj.optString("lat").toDoubleOrNull()
                    val lon = obj.optString("lon").toDoubleOrNull()

                    if (displayName.isNotBlank() && lat != null && lon != null) {
                        val (title, subtitle) = splitDisplayName(displayName)
                        results.add(
                            SearchResultDto(
                                name = title,
                                subtitle = subtitle,
                                latitude = lat,
                                longitude = lon
                            )
                        )
                    }
                }

                results
            } finally {
                connection.disconnect()
            }
        }

    /**
     * Nominatim renvoie une adresse complète du type :
     * "Kedge Business School, Avenue Gustave Eiffel, Talence, Gironde, Nouvelle-Aquitaine, France métropolitaine, 33400, France"
     * On garde le 1er segment comme titre, et les 2-3 suivants comme sous-titre court.
     */
    private fun splitDisplayName(displayName: String): Pair<String, String> {
        val parts = displayName.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.isEmpty()) return displayName to ""
        val title = parts.first()
        val subtitle = parts.drop(1).take(2).joinToString(", ")
        return title to subtitle
    }
}