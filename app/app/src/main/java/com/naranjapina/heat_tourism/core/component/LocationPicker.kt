package com.naranjapina.heat_tourism.core.component

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.core.utils.LocationUtils
import com.naranjapina.heat_tourism.data.SampleDestinations

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPicker(
    modifier: Modifier = Modifier,
    fallbackLabel: String = "Plaza de Bolivar, Bogota",
    onChangeRequested: () -> Unit = {}
) {
    val context = LocalContext.current

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var label by remember { mutableStateOf(fallbackLabel) }
    var detected by remember { mutableStateOf(false) }

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        val granted = permissionsState.permissions.any { it.status.isGranted }
        if (!granted) return@LaunchedEffect
        val point = LocationUtils.getCurrentOrFallbackPoint(
            context = context,
            fallbackLng = SampleDestinations.BOGOTA_CENTER_LNG,
            fallbackLat = SampleDestinations.BOGOTA_CENTER_LAT
        )
        val readable = LocationUtils.reverseGeocode(context, point.second, point.first)
        if (!readable.isNullOrBlank()) {
            label = readable
            detected = true
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(R.color.red_100).copy(.15f),
                shape = RoundedCornerShape(15.dp)
            )
            .border(
                color = colorResource(R.color.red_100),
                width = 1.dp,
                shape = RoundedCornerShape(15.dp)
            )
            .clickable {
                if (!permissionsState.allPermissionsGranted) {
                    permissionsState.launchMultiplePermissionRequest()
                } else {
                    onChangeRequested()
                }
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Image(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = "Ubicacion",
            colorFilter = ColorFilter.tint(
                colorResource(R.color.red_400)
            ),
            modifier = Modifier
                .background(
                    color = colorResource(R.color.red_100).copy(.4f),
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(10.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = if (detected) "Ubicacion detectada" else "Tu ubicacion",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = label)
            }
            Text(
                text = if (permissionsState.allPermissionsGranted) "Cambiar" else "Activar",
                color = colorResource(R.color.red_400)
            )
        }
    }
}
