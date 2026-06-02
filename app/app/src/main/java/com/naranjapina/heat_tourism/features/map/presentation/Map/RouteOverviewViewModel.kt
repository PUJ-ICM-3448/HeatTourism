package com.naranjapina.heat_tourism.features.map.presentation.Map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naranjapina.heat_tourism.features.map.presentation.model.MapPoint
import com.naranjapina.heat_tourism.features.social.data.repository.DestinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouteOverviewViewModel(
    private val repository: DestinationRepository = DestinationRepository()
) : ViewModel() {

    private val _destination = MutableStateFlow<MapPoint?>(null)
    val destination: StateFlow<MapPoint?> = _destination

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadDestination(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _destination.value = repository.getDestinationById(id)
            _isLoading.value = false
        }
    }

    fun updateDestination(destination: MapPoint, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.updateDestination(destination)
            if (success) {
                _destination.value = destination
            }
            onResult(success)
        }
    }
}
