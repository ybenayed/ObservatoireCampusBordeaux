package com.ObservatoireCampus.mobile.viewmodel.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient

class LocationViewModelFactory(
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LocationViewModel(fusedLocationClient) as T
    }
}