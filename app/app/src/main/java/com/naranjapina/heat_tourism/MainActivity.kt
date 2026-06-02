package com.naranjapina.heat_tourism

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import com.mapbox.common.MapboxOptions
import com.naranjapina.heat_tourism.core.design.theme.HeatTourismTheme
import com.naranjapina.heat_tourism.core.navigation.NavigationStack
import com.naranjapina.heat_tourism.core.utils.MapboxConfig
import com.naranjapina.heat_tourism.shared.notifications.NotificationCenter
import com.naranjapina.heat_tourism.shared.social.NotificationRepo

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = MapboxConfig.accessToken(this)
        if (token.isNotBlank()) {
            MapboxOptions.accessToken = token
        }
        NotificationCenter.createChannel(this)
        requestNotificationPermissionIfNeeded()
        NotificationRepo().syncFcmTokenForCurrentUser()

        enableEdgeToEdge()
        setContent {
            HeatTourismTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(R.color.beige)),
                    color = colorResource(R.color.beige)
                ) {
                    NavigationStack()
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
