package com.ObservatoireCampus.mobile.viewmodel.station

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ObservatoireCampus.mobile.repository.station.StationTBRepository
import com.ObservatoireCampus.mobile.repository.station.StationVRepository
import com.ObservatoireCampus.mobile.repository.station.StationTerRepository

class StationTBViewModelFactory(
    private val repository: StationTBRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return StationTBViewModel(repository) as T
    }
}

class StationVViewModelFactory(
    private val repository: StationVRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return StationVViewModel(repository) as T
    }
}
class StationTerViewModelFactory(
    private val repository: StationTerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return StationTerViewModel(repository) as T
    }
}