package com.naranjapina.heat_tourism.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.utils.AmbientTemperatureState

/**
 * Widget que compara la temperatura ambiente local (sensor) con la del destino (mock).
 * Si el dispositivo no tiene sensor de temperatura, muestra solo la del destino con un aviso.
 */
@Composable
fun TemperatureWidget(
    modifier: Modifier = Modifier,
    state: AmbientTemperatureState,
    destinationName: String,
    destinationTempC: Float
) {
    val localTempText = when {
        !state.isAvailable -> "—"
        state.temperature == null -> "..."
        else -> "${state.temperature.toInt()}°C"
    }

    val hint = when {
        !state.isAvailable ->
            "Tu sensor de temperatura no esta disponible"

        state.temperature == null ->
            "Leyendo temperatura local..."

        destinationTempC - state.temperature > 5f ->
            "¡Hara mas calor alla! Lleva ropa ligera 🥵"

        state.temperature - destinationTempC > 5f ->
            "Va a estar mas fresco. Lleva un abrigo 🧥"

        else ->
            "Temperaturas parecidas, viaja comodo 😎"
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = colorResource(R.color.red_50),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🌡️ Aqui",
                    color = colorResource(R.color.neutral_400),
                    fontSize = 12.sp
                )
                Text(
                    text = localTempText,
                    color = colorResource(R.color.red_400),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(colorResource(R.color.neutral_200))
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🔥 $destinationName",
                    color = colorResource(R.color.neutral_400),
                    fontSize = 12.sp
                )
                Text(
                    text = "${destinationTempC.toInt()}°C",
                    color = colorResource(R.color.orange_400),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = hint,
            color = colorResource(R.color.neutral_400),
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
