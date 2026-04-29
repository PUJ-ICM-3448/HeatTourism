package com.naranjapina.heat_tourism.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * Estado del sensor de temperatura ambiente.
 *
 * @property temperature Temperatura en grados Celsius. null si el sensor no esta disponible
 *                       o aun no ha emitido lectura.
 * @property isAvailable true si el dispositivo tiene sensor de temperatura ambiente.
 */
data class AmbientTemperatureState(
    val temperature: Float?,
    val isAvailable: Boolean
)

/**
 * Composable que se suscribe al sensor TYPE_AMBIENT_TEMPERATURE.
 * Muchos dispositivos (incluyendo la mayoria de emuladores) no tienen este sensor:
 * en ese caso devuelve isAvailable=false y la UI puede mostrar un fallback.
 */
@Composable
fun rememberAmbientTemperature(): AmbientTemperatureState {
    val context = LocalContext.current
    var temperature by remember { mutableStateOf<Float?>(null) }
    var isAvailable by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val tempSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        if (sensorManager == null || tempSensor == null) {
            isAvailable = false
            return@DisposableEffect onDispose { }
        }

        isAvailable = true

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                temperature = event.values[0]
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, tempSensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return AmbientTemperatureState(temperature = temperature, isAvailable = isAvailable)
}
