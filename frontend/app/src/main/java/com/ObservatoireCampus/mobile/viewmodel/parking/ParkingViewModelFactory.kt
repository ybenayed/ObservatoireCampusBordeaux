package com.ObservatoireCampus.mobile.viewmodel.parking


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ObservatoireCampus.mobile.repository.ParkingRepository

class ParkingViewModelFactory(
    private val repository: ParkingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ParkingViewModel(repository) as T
    }
}