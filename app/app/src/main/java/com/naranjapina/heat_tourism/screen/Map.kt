package com.naranjapina.heat_tourism.screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R
import com.naranjapina.heat_tourism.component.DestinationCardData
import com.naranjapina.heat_tourism.component.HorizontalDestinationCard
import com.naranjapina.heat_tourism.component.JustInputText
import com.naranjapina.heat_tourism.component.LazyFilterChipRow
import com.naranjapina.heat_tourism.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.utils.bottomBorder

@Composable
fun MapScreen(navController: NavHostController) {
    MenuBottonLayout(
        activeName = "map", navController = navController
    ) { paddingValues ->
        Column (
            modifier = Modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .bottomBorder(
                        strokeWidth = 1.dp,
                        color = colorResource(R.color.red_100)
                    )
                    .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp, 0.dp)
                    .padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text="Mapa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }


            Image(
                painter = painterResource(R.drawable.map_placeholder),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

