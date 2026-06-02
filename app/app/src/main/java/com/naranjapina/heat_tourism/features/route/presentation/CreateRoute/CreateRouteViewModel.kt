package com.naranjapina.heat_tourism.features.route.presentation.CreateRoute

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naranjapina.heat_tourism.features.route.data.model.Route
import com.naranjapina.heat_tourism.features.route.data.repository.RouteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateRouteState(
    val name: String = "",
    val description: String = "",
    val duration: String = "",
    val price: String = "",
    val stops: List<String> = emptyList(),
    val currentStopInput: String = "",
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CreateRouteViewModel(
    private val repository: RouteRepository = RouteRepository()
) : ViewModel() {
    private val _state = MutableStateFlow(CreateRouteState())
    val state: StateFlow<CreateRouteState> = _state.asStateFlow()

    fun onNameChange(newName: String) {
        _state.update { it.copy(name = newName) }
    }

    fun onDescriptionChange(newDescription: String) {
        _state.update { it.copy(description = newDescription) }
    }

    fun onDurationChange(newDuration: String) {
        _state.update { it.copy(duration = newDuration) }
    }

    fun onPriceChange(newPrice: String) {
        _state.update { it.copy(price = newPrice) }
    }

    fun onCurrentStopInputChange(newInput: String) {
        _state.update { it.copy(currentStopInput = newInput) }
    }

    fun addStop() {
        val stop = _state.value.currentStopInput.trim()
        if (stop.isNotEmpty()) {
            _state.update {
                it.copy(
                    stops = it.stops + stop,
                    currentStopInput = ""
                )
            }
        }
    }

    fun removeStop(stop: String) {
        _state.update {
            it.copy(
                stops = it.stops - stop
            )
        }
    }

    fun saveRoute() {
        val currentState = _state.value
        if (currentState.name.isBlank()) {
            _state.update { it.copy(errorMessage = "El nombre de la ruta es obligatorio") }
            return
        }
        if (currentState.stops.isEmpty()) {
            _state.update { it.copy(errorMessage = "Debe agregar al menos una parada/nodo") }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val newRoute = Route(
                    name = currentState.name,
                    description = currentState.description,
                    duration = currentState.duration,
                    price = currentState.price,
                    stops = currentState.stops
                )
                repository.saveRoute(newRoute)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isSavedSuccess = true,
                        name = "",
                        description = "",
                        duration = "",
                        price = "",
                        stops = emptyList(),
                        currentStopInput = ""
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al guardar: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun resetSaveStatus() {
        _state.update { it.copy(isSavedSuccess = false, errorMessage = null) }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }
}
