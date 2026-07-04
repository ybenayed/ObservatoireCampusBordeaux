package com.ObservatoireCampus.mobile.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.repository.MapRepository
import com.ObservatoireCampus.mobile.model.CampusDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MapRepository(application.applicationContext)

    private val _campusList = MutableStateFlow<List<CampusDto>>(emptyList())
    val campusList: StateFlow<List<CampusDto>> = _campusList

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCampus()
    }

    fun loadCampus() {
        viewModelScope.launch {
            try {
                Log.d("MapViewModel", ">>> Appel API en cours...")
                val result = repository.getCampus()
                Log.d("MapViewModel", ">>> Reçu ${result.size} campus")
                _campusList.value = result
                _error.value = null
            } catch (e: Exception) {
                Log.e("MapViewModel", ">>> ERREUR: ${e.message}", e)
                _error.value = e.message
            }
        }
    }
}