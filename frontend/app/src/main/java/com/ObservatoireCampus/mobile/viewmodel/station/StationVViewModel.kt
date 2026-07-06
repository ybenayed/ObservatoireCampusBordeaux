package com.ObservatoireCampus.mobile.viewmodel.station

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.model.station.StationVDetailDto
import com.ObservatoireCampus.mobile.model.station.StationVPositionDto
import com.ObservatoireCampus.mobile.repository.station.StationVRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StationVViewModel(
    private val repository: StationVRepository
) : ViewModel() {

    private var allPositions: List<StationVPositionDto> = emptyList()

    private val _layers = MutableStateFlow<List<LayerItemUiState>>(emptyList())
    val layers: StateFlow<List<LayerItemUiState>> = _layers

    private val _visiblePositions = MutableStateFlow<List<StationVPositionDto>>(emptyList())
    val visiblePositions: StateFlow<List<StationVPositionDto>> = _visiblePositions

    private val _selectedDetail = MutableStateFlow<StationVDetailDto?>(null)
    val selectedDetail: StateFlow<StationVDetailDto?> = _selectedDetail

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
                _layers.value = listOf(
                    LayerItemUiState(key = "VELO", label = "Velo", count = positions.size.toLong(), visible = false)
                )
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
        val active = _layers.value.any { it.visible }
        _visiblePositions.value = if (active) allPositions else emptyList()
    }

    fun onStationClicked(station: StationVPositionDto) {
        _selectedDetail.value = null
        _bubbleLoading.value = true

        viewModelScope.launch {
            try {
                _selectedDetail.value = repository.getDetail(station.stationId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _bubbleLoading.value = false
            }
        }
    }

    fun closeBubble() {
        _selectedDetail.value = null
    }
}