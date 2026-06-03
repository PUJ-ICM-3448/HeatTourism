package com.naranjapina.heat_tourism.features.route.presentation.Purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.naranjapina.heat_tourism.features.travel.data.model.GrupoViaje
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class PurchasesState(
    val currentRoutes: List<GrupoViaje> = emptyList(),
    val futureRoutes: List<GrupoViaje> = emptyList(),
    val pastRoutes: List<GrupoViaje> = emptyList(),
    val isLoading: Boolean = false
)

class PurchasesViewModel : ViewModel() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val _state = MutableStateFlow(PurchasesState())
    val state = _state.asStateFlow()

    fun loadUserPurchases(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Buscamos los grupos donde el usuario es miembro
                val groupIds = db.collectionGroup("miembros")
                    .whereEqualTo("__name__", userId) // Buscamos por el ID del documento en la subcolección
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.reference.parent.parent?.id }

                if (groupIds.isEmpty()) {
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }

                val allGroups = mutableListOf<GrupoViaje>()
                for (id in groupIds) {
                    val doc = db.collection("grupos_viaje").document(id).get().await()
                    doc.toObject(GrupoViaje::class.java)?.let { allGroups.add(it) }
                }

                _state.update {
                    it.copy(
                        currentRoutes = allGroups.filter { g -> g.status == "ACTIVE" },
                        futureRoutes = allGroups.filter { g -> g.status == "PENDING" },
                        pastRoutes = allGroups.filter { g -> g.status == "COMPLETED" || g.status == "CANCELLED" },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
