package com.naranjapina.heat_tourism.features.home.presentation.Home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naranjapina.heat_tourism.features.home.domain.model.User
import com.naranjapina.heat_tourism.features.travel.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import com.naranjapina.heat_tourism.core.component.DestinationCardData
import com.naranjapina.heat_tourism.features.route.data.repository.RouteRepository
import com.naranjapina.heat_tourism.features.social.data.model.Post
import com.naranjapina.heat_tourism.features.social.data.repository.SocialRepository
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

    import com.naranjapina.heat_tourism.features.travel.data.model.GrupoViaje
import com.naranjapina.heat_tourism.features.travel.data.model.MiembroGrupo
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    private val travelRepository = TravelRepository()
    private val routeRepository = RouteRepository()
    private val socialRepository = SocialRepository()
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _user = MutableStateFlow(User(tipo = "GENERAL"))
    val user: StateFlow<User> = _user

    private val _destinations = MutableStateFlow<List<DestinationCardData>>(emptyList())
    val destinations: StateFlow<List<DestinationCardData>> = _destinations

    private val _publications = MutableStateFlow<List<Post>>(emptyList())
    val publications: StateFlow<List<Post>> = _publications

    private val _activeGroupId = MutableStateFlow<String?>(null)
    val activeGroupId: StateFlow<String?> = _activeGroupId

    private val _activeGroup = MutableStateFlow<GrupoViaje?>(null)
    val activeGroup: StateFlow<GrupoViaje?> = _activeGroup

    private val _members = MutableStateFlow<List<MiembroGrupo>>(emptyList())
    val members: StateFlow<List<MiembroGrupo>> = _members

    init {
        fetchUserData()
        loadHomeData()
        fetchActiveGroup()
    }

    private fun fetchUserData() {
        val currentUserId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users")
                    .whereEqualTo("authId", currentUserId)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    val roles = snapshot.documents[0].get("roles") as? List<String> ?: emptyList()
                    val userType = when {
                        roles.contains("ADMIN") -> "ADMIN"
                        roles.contains("COORDINATOR") -> "COORDINATOR"
                        else -> "GENERAL"
                    }
                    _user.value = User(tipo = userType)
                }
            } catch (e: Exception) {
                _user.value = User(tipo = "GENERAL")
            }
        }
    }

    private fun fetchActiveGroup() {
        val currentUserId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val groupId = travelRepository.getActiveGroupIdForUser(currentUserId)
            if (groupId != null) {
                _activeGroupId.value = groupId
                travelRepository.getGroupDetails(groupId) { group ->
                    _activeGroup.value = group
                }
                travelRepository.getGroupMembers(groupId) { memberList ->
                    _members.value = memberList
                }
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _destinations.value = routeRepository.getPopularRoutes()
            _publications.value = socialRepository.getGlobalPosts()
        }
    }

    fun emitAlert(groupId: String, message: String) {
        viewModelScope.launch {
            try {
                travelRepository.emitAlert(groupId, message)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun startAttendance(groupId: String) {
        viewModelScope.launch {
            try {
                travelRepository.updateAttendanceStatus(groupId, true)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun stopAttendance(groupId: String) {
        viewModelScope.launch {
            try {
                travelRepository.updateAttendanceStatus(groupId, false)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun markPresence(groupId: String, userId: String, isPresent: Boolean) {
        viewModelScope.launch {
            try {
                travelRepository.markMemberAttendance(groupId, userId, isPresent)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun listenForAlerts(context: Context, groupId: String) {
        travelRepository.listenForAlerts(context, groupId)
    }
}
