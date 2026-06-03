package com.naranjapina.heat_tourism.features.route.presentation.Buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naranjapina.heat_tourism.data.auth.model.AuthUser
import com.naranjapina.heat_tourism.features.route.data.model.Route
import com.naranjapina.heat_tourism.features.route.data.repository.RouteRepository
import com.naranjapina.heat_tourism.features.travel.data.model.GrupoViaje
import com.naranjapina.heat_tourism.features.travel.data.model.MiembroGrupo
import com.naranjapina.heat_tourism.features.travel.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BuyState {
    object Idle : BuyState()
    object Loading : BuyState()
    data class Success(val groupId: String) : BuyState()
    data class Error(val message: String) : BuyState()
}

class BuyViewModel(
    private val travelRepository: TravelRepository = TravelRepository(),
    private val routeRepository: RouteRepository = RouteRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<BuyState>(BuyState.Idle)
    val state = _state.asStateFlow()

    private val _route = MutableStateFlow<Route?>(null)
    val route = _route.asStateFlow()

    fun loadRoute(routeId: String) {
        viewModelScope.launch {
            val routeData = routeRepository.getRouteById(routeId)
            _route.value = routeData
        }
    }

    fun purchaseRoute(routeId: String, routeName: String, user: AuthUser) {
        viewModelScope.launch {
            _state.value = BuyState.Loading
            try {
                val grupo = GrupoViaje(
                    routeId = routeId,
                    routeName = routeName,
                    status = "PENDING"
                )
                val miembro = MiembroGrupo(
                    id = user.id,
                    fullName = user.fullName.ifEmpty { user.email },
                    role = "TOURIST",
                    checkInStatus = "PENDING",
                    isPresent = false
                )
                val groupId = travelRepository.createGroupAndAddMember(grupo, miembro)
                _state.value = BuyState.Success(groupId)
            } catch (e: Exception) {
                _state.value = BuyState.Error(e.localizedMessage ?: "Ocurrió un error al procesar la compra")
            }
        }
    }

    fun resetState() {
        _state.value = BuyState.Idle
    }
}
