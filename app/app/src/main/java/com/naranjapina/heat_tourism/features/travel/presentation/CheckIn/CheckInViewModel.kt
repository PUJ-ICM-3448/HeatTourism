package com.naranjapina.heat_tourism.features.travel.presentation.CheckIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naranjapina.heat_tourism.features.travel.data.model.MiembroGrupo
import com.naranjapina.heat_tourism.features.travel.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckInViewModel(
    private val repository: TravelRepository = TravelRepository()
) : ViewModel() {

    private val _members = MutableStateFlow<List<MiembroGrupo>>(emptyList())
    val members: StateFlow<List<MiembroGrupo>> = _members

    fun loadMembers(groupId: String) {
        repository.getGroupMembers(groupId) { updatedMembers ->
            _members.value = updatedMembers
        }
    }

    fun updateStatus(groupId: String, userId: String, status: String) {
        viewModelScope.launch {
            repository.updateCheckInStatus(groupId, userId, status)
        }
    }

    fun markAttendance(groupId: String, userId: String, isPresent: Boolean) {
        viewModelScope.launch {
            repository.markMemberAttendance(groupId, userId, isPresent)
        }
    }
}
