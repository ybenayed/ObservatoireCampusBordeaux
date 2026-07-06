package com.ObservatoireCampus.mobile.viewmodel.freevehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ObservatoireCampus.mobile.repository.freevehicle.FreeVehicleRepository

class FreeVehicleViewModelFactory(
    private val repository: FreeVehicleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FreeVehicleViewModel(repository) as T
    }
}