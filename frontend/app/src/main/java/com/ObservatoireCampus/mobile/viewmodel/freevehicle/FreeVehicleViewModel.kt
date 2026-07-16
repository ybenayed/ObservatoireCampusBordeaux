package com.ObservatoireCampus.mobile.viewmodel.freevehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehiclePositionDto
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehicleDetailDto
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.repository.freevehicle.FreeVehicleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FreeVehicleViewModel(
    private val repository: FreeVehicleRepository
) : ViewModel() {

    private var allPositions: List<FreeVehiclePositionDto> = emptyList()

    private val _layers = MutableStateFlow<List<LayerItemUiState>>(emptyList())
    val layers: StateFlow<List<LayerItemUiState>> = _layers

    private val _visiblePositions = MutableStateFlow<List<FreeVehiclePositionDto>>(emptyList())
    val visiblePositions: StateFlow<List<FreeVehiclePositionDto>> = _visiblePositions

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var autoRefreshStarted = false

    val masterActive: Boolean
        get() = _layers.value.isNotEmpty() && _layers.value.all { it.visible }

    private val _selectedVehicleId = MutableStateFlow<String?>(null)
    val selectedVehicleId: StateFlow<String?> = _selectedVehicleId

    private val _selectedVehicle = MutableStateFlow<FreeVehicleDetailDto?>(null)
    val selectedVehicle: StateFlow<FreeVehicleDetailDto?> = _selectedVehicle

    private val _bubbleLoading = MutableStateFlow(false)
    val bubbleLoading: StateFlow<Boolean> = _bubbleLoading

    fun onVehicleClicked(bikeId: String) {
        android.util.Log.d("FreeVehicle", "bikeId cliqué = [$bikeId]")
        _selectedVehicleId.value = bikeId
        _selectedVehicle.value = null
        _bubbleLoading.value = true
        viewModelScope.launch {
            try {
                _selectedVehicle.value = repository.getVehicleDetail(bikeId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _bubbleLoading.value = false
            }
        }
    }

    fun closeBubble() {
        _selectedVehicleId.value = null
        _selectedVehicle.value = null
    }

    // Chargement initial : types + comptage
    fun loadStations() {
        viewModelScope.launch {
            try {
                val counts = repository.getTypesCount()
                // On garde la visibilité actuelle si le layer existait déjà
                val previousVisibility = _layers.value.associate { it.key to it.visible }

                _layers.value = counts.map { c ->
                    LayerItemUiState(
                        key = c.vehicleTypeId,
                        // MODIFICATION : On stocke la clé brute. L'UI (Compose) se chargera
                        // de la traduire asynchronement à l'écran.
                        label = c.vehicleTypeId,
                        count = c.count,
                        visible = previousVisibility[c.vehicleTypeId] ?: false
                    )
                }
                _error.value = null
                refreshPositions()
                startAutoRefresh()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // REFRESH AUTOMATIQUE TOUTES LES 10 SECONDES
    private fun startAutoRefresh() {
        if (autoRefreshStarted) return
        autoRefreshStarted = true
        viewModelScope.launch {
            while (true) {
                delay(10_000)
                refreshPositions()
            }
        }
    }

    private suspend fun refreshPositions() {
        try {
            allPositions = repository.getPositions()
            recomputeVisiblePositions()
        } catch (e: Exception) {
            _error.value = e.message
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
        _visiblePositions.value = allPositions.filter { it.vehicleTypeId in activeKeys }
    }
}