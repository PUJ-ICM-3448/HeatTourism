package com.naranjapina.heat_tourism.features.travel.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.naranjapina.heat_tourism.features.travel.data.model.GrupoViaje
import com.naranjapina.heat_tourism.features.travel.data.model.MiembroGrupo
import kotlinx.coroutines.tasks.await

class TravelRepository {
    private val db: FirebaseFirestore = Firebase.firestore
    // ... código existente ...
        private val groupsCollection = db.collection("grupos_viaje")

        /**
         * Busca el grupo activo al que pertenece un usuario.
         */
        suspend fun getActiveGroupIdForUser(userId: String): String? {
            val snapshot = groupsCollection
                .whereEqualTo("status", "ACTIVE")
                .get()
                .await()

            for (doc in snapshot.documents) {
                val memberDoc = doc.reference.collection("miembros").document(userId).get().await()
                if (memberDoc.exists()) {
                    return doc.id
                }
            }
            return null
        }

        /**
         * Obtiene los detalles de un grupo en tiempo real.
         */
        fun getGroupDetails(groupId: String, onUpdate: (GrupoViaje?) -> Unit) {
            groupsCollection.document(groupId)
                .addSnapshotListener { snapshot, _ ->
                    val group = snapshot?.toObject(GrupoViaje::class.java)
                    onUpdate(group)
                }
        }

        /**
         * Crea un nuevo Grupo de Viaje (GrupoViaje) y agrega el miembro inicial en la subcolección "miembros".
         */
    // ... código existente ...
    suspend fun createGroupAndAddMember(grupo: GrupoViaje, miembro: MiembroGrupo): String {
        val payload = hashMapOf(
            "routeId" to grupo.routeId,
            "routeName" to grupo.routeName,
            "createdAt" to grupo.createdAt,
            "status" to grupo.status,
            "alert" to grupo.alert,
            "attendanceStarted" to grupo.attendanceStarted
        )

        val docRef = groupsCollection.add(payload).await()
        val groupId = docRef.id

        val memberId = miembro.id ?: throw IllegalArgumentException("El ID del miembro no puede ser nulo")

        val memberPayload = hashMapOf(
            "fullName" to miembro.fullName,
            "role" to miembro.role,
            "checkInStatus" to miembro.checkInStatus,
            "isPresent" to miembro.isPresent
        )

        groupsCollection.document(groupId)
            .collection("miembros")
            .document(memberId)
            .set(memberPayload)
            .await()

        return groupId
    }

    /**
     * Obtiene los miembros de un grupo en tiempo real.
     */
    fun getGroupMembers(groupId: String, onUpdate: (List<MiembroGrupo>) -> Unit) {
        groupsCollection.document(groupId)
            .collection("miembros")
            .addSnapshotListener { snapshot, _ ->
                val members = snapshot?.toObjects(MiembroGrupo::class.java) ?: emptyList()
                onUpdate(members)
            }
    }

    /**
     * Actualiza el estado de Check-In de un pasajero.
     */
    suspend fun updateCheckInStatus(groupId: String, userId: String, status: String) {
        groupsCollection.document(groupId)
            .collection("miembros")
            .document(userId)
            .update("checkInStatus", status)
            .await()
    }

    /**
     * Actualiza la alerta de un grupo de viaje.
     */
    suspend fun emitAlert(groupId: String, alertMessage: String) {
        groupsCollection.document(groupId)
            .update("alert", alertMessage)
            .await()
    }

    /**
     * Escucha cambios en un grupo de viaje para detectar alertas y vibrar.
     */
    fun listenForAlerts(context: Context, groupId: String) {
        groupsCollection.document(groupId)
            .addSnapshotListener { snapshot, _ ->
                val alert = snapshot?.getString("alert")
                if (!alert.isNullOrEmpty()) {
                    triggerVibration(context)
                    showNotification(context, alert)
                }
            }
    }

    /**
     * Obtiene los detalles de un grupo de viaje.
     */
    suspend fun getGroupDetailsOnce(groupId: String): GrupoViaje? {
        return groupsCollection.document(groupId).get().await().toObject(GrupoViaje::class.java)
    }

    /**
     * Obtiene los miembros de un grupo de viaje.
     */
    suspend fun getGroupMembersOnce(groupId: String): List<MiembroGrupo> {
        return groupsCollection.document(groupId).collection("miembros").get().await().toObjects(MiembroGrupo::class.java)
    }

    /**
     * Inicia o finaliza el llamado a lista (asistencia) en un grupo de viaje.
     */
    suspend fun updateAttendanceStatus(groupId: String, isStarted: Boolean) {
        groupsCollection.document(groupId)
            .update("attendanceStarted", isStarted)
            .await()
    }

    /**
     * Marca la presencia de un miembro individual durante el llamado a lista.
     */
    suspend fun markMemberAttendance(groupId: String, userId: String, isPresent: Boolean) {
        groupsCollection.document(groupId)
            .collection("miembros")
            .document(userId)
            .update("isPresent", isPresent)
            .await()
    }

    private fun triggerVibration(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 500, 200, 500), -1)
        }
    }

    private fun showNotification(context: Context, message: String) {
        val channelId = "travel_alerts"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alertas de Viaje", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("ALERTA DE SEGURIDAD")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
