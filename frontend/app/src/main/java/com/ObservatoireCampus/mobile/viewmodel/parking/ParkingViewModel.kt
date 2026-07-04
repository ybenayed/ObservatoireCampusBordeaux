package com.ObservatoireCampus.mobile.viewmodel.parking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.parking.ParkingPositionDto
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.repository.ParkingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ParkingViewModel(
    private val repository: ParkingRepository
) : ViewModel() {

    private var allPositions: List<ParkingPositionDto> = emptyList()

    private val _parkingLayers = MutableStateFlow<List<LayerItemUiState>>(emptyList())
    val parkingLayers: StateFlow<List<LayerItemUiState>> = _parkingLayers

    private val _visiblePositions = MutableStateFlow<List<ParkingPositionDto>>(emptyList())
    val visiblePositions: StateFlow<List<ParkingPositionDto>> = _visiblePositions

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val masterActive: Boolean
        get() = _parkingLayers.value.isNotEmpty() && _parkingLayers.value.all { it.visible }

    fun loadParking() {
        viewModelScope.launch {
            try {
                android.util.Log.d("ParkingViewModel", ">>> Appel API parking en cours...")
                val positions = repository.getAllPositions()
                android.util.Log.d("ParkingViewModel", ">>> Positions recues: ${positions.size}")
                val counts = repository.getCountByType()
                android.util.Log.d("ParkingViewModel", ">>> Types recus: ${counts.size}")

                allPositions = positions
                _parkingLayers.value = counts.map { count ->
                    LayerItemUiState(key = count.taType, label = count.taType, count = count.count, visible = false)
                }
                _error.value = null
            } catch (e: Exception) {
                android.util.Log.e("ParkingViewModel", ">>> ERREUR: ${e.message}", e)
                _error.value = e.message
            }
        }
    }

    fun toggleType(key: String) {
        _parkingLayers.value = _parkingLayers.value.map { item ->
            if (item.key == key) item.copy(visible = !item.visible) else item
        }
        recomputeVisiblePositions()
    }

    fun toggleMaster() {
        val shouldActivateAll = !masterActive
        _parkingLayers.value = _parkingLayers.value.map { it.copy(visible = shouldActivateAll) }
        recomputeVisiblePositions()
    }

    private fun recomputeVisiblePositions() {
        val activeKeys = _parkingLayers.value.filter { it.visible }.map { it.key }.toSet()
        _visiblePositions.value = allPositions.filter { it.taType in activeKeys }
    }
}