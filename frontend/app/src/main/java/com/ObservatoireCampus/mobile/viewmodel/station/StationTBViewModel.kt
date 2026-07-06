package com.ObservatoireCampus.mobile.viewmodel.station

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.model.station.PassageDto
import com.ObservatoireCampus.mobile.model.station.StationTBPositionDto
import com.ObservatoireCampus.mobile.repository.station.StationTBRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StationTBViewModel(
    private val repository: StationTBRepository
) : ViewModel() {

    private var allPositions: List<StationTBPositionDto> = emptyList()

    private val _layers = MutableStateFlow<List<LayerItemUiState>>(emptyList())
    val layers: StateFlow<List<LayerItemUiState>> = _layers

    private val _visiblePositions = MutableStateFlow<List<StationTBPositionDto>>(emptyList())
    val visiblePositions: StateFlow<List<StationTBPositionDto>> = _visiblePositions

    // ─── Etat de la bulle (station cliquee sur la carte)
    private val _selectedStation = MutableStateFlow<StationTBPositionDto?>(null)
    val selectedStation: StateFlow<StationTBPositionDto?> = _selectedStation

    private val _passages = MutableStateFlow<List<PassageDto>>(emptyList())
    val passages: StateFlow<List<PassageDto>> = _passages

    private val _bubbleLoading = MutableStateFlow(false)
    val bubbleLoading: StateFlow<Boolean> = _bubbleLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val masterActive: Boolean
        get() = _layers.value.isNotEmpty() && _layers.value.all { it.visible }

    fun loadStations() {
        viewModelScope.launch {
            try {
                val positions = repository.getPositions()
                allPositions = positions

                val counts = positions.groupingBy { it.mode }.eachCount()
                _layers.value = listOf("TRAM", "BUS").map { mode ->
                    LayerItemUiState(
                        key = mode,
                        label = if (mode == "TRAM") "Tram" else "Bus",
                        count = (counts[mode] ?: 0).toLong(),
                        visible = false
                    )
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleType(key: String) {
        _layers.value = _layers.value.map { item ->
            if (item.key == key) item.copy(visible = !item.visible) else item
        }
        recomputeVisiblePositions()
    }

    fun toggleMaster() {
        val shouldActivateAll = !masterActive
        _layers.value = _layers.value.map { it.copy(visible = shouldActivateAll) }
        recomputeVisiblePositions()
    }

    private fun recomputeVisiblePositions() {
        val activeKeys = _layers.value.filter { it.visible }.map { it.key }.toSet()
        _visiblePositions.value = allPositions.filter { it.mode in activeKeys }
    }

    // Appele depuis la carte quand l'utilisateur tape sur un marqueur
    fun onStationClicked(station: StationTBPositionDto) {
        _selectedStation.value = station
        _passages.value = emptyList()
        _bubbleLoading.value = true

        viewModelScope.launch {
            try {
                _passages.value = repository.getPassages(station.stopId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _bubbleLoading.value = false
            }
        }
    }

    fun closeBubble() {
        _selectedStation.value = null
        _passages.value = emptyList()
    }
}