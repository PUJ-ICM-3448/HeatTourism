package com.naranjapina.heat_tourism

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.mapbox.common.MapboxOptions
import com.naranjapina.heat_tourism.navigation.NavigationStack
import com.naranjapina.heat_tourism.ui.theme.HeatTourismTheme
import com.naranjapina.heat_tourism.utils.MapboxConfig

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val token = MapboxConfig.accessToken(this)
    if (token.isNotBlank()) {
      MapboxOptions.accessToken = token
    }

    enableEdgeToEdge()
    setContent {
      HeatTourismTheme {
        Surface(
          modifier = Modifier.background(colorResource(R.color.beige))
        ) {
          NavigationStack()
        }
      }
    }
  }
}