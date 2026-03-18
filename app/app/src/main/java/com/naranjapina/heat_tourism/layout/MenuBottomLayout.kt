package com.naranjapina.heat_tourism.layout

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.navigation.Screen


@Composable
fun RowScope.MenuItem(painter: Painter, label: String, active: Boolean, onClick: () -> Unit = {}) {
    val color: Color;

    if(active) {
        color = colorResource(R.color.red_400)
    } else {
        color = colorResource(R.color.dark_beige)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f).clickable(
            onClick = onClick
        ),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = label,
            colorFilter = ColorFilter.tint(
                color
            ),
            modifier = Modifier.height(25.dp).width(25.dp)
        )
        Text(
            text = label,
            modifier = Modifier,
            color = color
        )
    }
}

@Composable
fun MenuBottonLayout( navController: NavController, activeName: String, Child: @Composable (paddingValues: PaddingValues) -> Unit) {

    Scaffold(
        containerColor = colorResource(R.color.beige),
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MenuItem(active = "home" == activeName,
                        label = "Inicio",
                        painter = painterResource(R.drawable.home)) {
                        navController.navigate(Screen.Home.name)
                    }
                    MenuItem(active = "searcher" == activeName,
                        label = "Buscador",
                        painter = painterResource(R.drawable.compass)) {
                        navController.navigate(Screen.Searcher.name)
                    }
                    MenuItem(active = "create" == activeName,
                        label = "Crear",
                        painter = painterResource(R.drawable.flame)) {

                        navController.navigate(Screen.CreatePost.name)
                    }
                    MenuItem(active= "map" == activeName,
                        label = "Mapa",
                        painter = painterResource(R.drawable.map),
                        onClick = {
                            navController.navigate(Screen.Map.name)
                        })
                    MenuItem(active= "profile" == activeName,
                        label = "Perfil",
                        painter = painterResource(R.drawable.person),
                        onClick = {
                            navController.navigate(Screen.Profile.name)
                        })
                }
            }
        }
    ) {paddingValues ->
        Child(paddingValues)
    }
}