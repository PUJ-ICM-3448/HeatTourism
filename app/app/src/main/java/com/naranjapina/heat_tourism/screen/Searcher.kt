package com.naranjapina.heat_tourism.screen
import com.naranjapina.heat_tourism.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.component.DestinationCard
import com.naranjapina.heat_tourism.component.DestinationCardData
import com.naranjapina.heat_tourism.component.HorizontalDestinationCard
import com.naranjapina.heat_tourism.component.JustInputText
import com.naranjapina.heat_tourism.component.LazyFilterChipRow
import com.naranjapina.heat_tourism.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.utils.bottomBorder

@Composable
fun SearcherScreen(navController: NavHostController) {
    var text by remember {
        mutableStateOf("")
    }

    MenuBottonLayout(activeName = "searcher", navController = navController) {paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(
                0.dp, 0.dp, 0.dp, paddingValues.calculateBottomPadding()
            )
        ) {
            item {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .bottomBorder(
                            strokeWidth = 1.dp,
                            color = colorResource(R.color.red_100)
                        )
                        .padding(0.dp, paddingValues.calculateTopPadding(), 0.dp, 0.dp)
                        .padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text(
                        text="Explorar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                    JustInputText(
                        modifier = Modifier.border(
                            width = 1.dp,
                            color = colorResource(R.color.red_50),
                            shape = RoundedCornerShape(15.dp)
                        ),
                        value = text,
                        placeholder = "Buscar destinos, lugares, eventos...",
                        icon = Icons.Outlined.Search,
                        changeValue = { text = it }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(5.dp))
                LazyFilterChipRow()
            }

            val destinations = listOf(1, 2, 3, 4)
            items(destinations.size) {
                HorizontalDestinationCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 15.dp, vertical = 8.dp),
                    data = DestinationCardData(
                        destinationName = "Bali, Indonesia",
                        destinationScore = 4.783f,
                        imgUrl = "https://www.outlooktravelmag.com/media/bali-1-1679062958.profileImage.2x-1536x884.webp",
                        contentDescription = ""
                    ),
                    navController = navController
                )
            }
        }
    }
}

