package com.naranjapina.heat_tourism.screen
import com.naranjapina.heat_tourism.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.component.JustInputText
import com.naranjapina.heat_tourism.component.LazyFilterChipRow
import com.naranjapina.heat_tourism.data.SampleDestinations
import com.naranjapina.heat_tourism.data.model.MapPoint
import com.naranjapina.heat_tourism.layout.MenuBottonLayout
import com.naranjapina.heat_tourism.navigation.Screen
import com.naranjapina.heat_tourism.utils.bottomBorder

@Composable
fun SearcherScreen(navController: NavHostController) {
    var text by remember {
        mutableStateOf("")
    }
    var selectedCategory by remember {
        mutableStateOf(SampleDestinations.ALL_CATEGORIES)
    }

    val categoryOptions = remember {
        SampleDestinations.categoryOptions()
    }

    val filteredDestinations = remember(text, selectedCategory) {
        val categoryFiltered = SampleDestinations.filterByCategory(
            destinations = SampleDestinations.bogotaDestinations,
            selectedCategory = selectedCategory
        )
        val query = text.trim().lowercase()
        if (query.isBlank()) {
            categoryFiltered
        } else {
            categoryFiltered.filter { point ->
                point.name.lowercase().contains(query) ||
                    point.description.lowercase().contains(query) ||
                    point.category.lowercase().contains(query)
            }
        }
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
                LazyFilterChipRow(
                    chips = categoryOptions.map { SampleDestinations.categoryLabel(it) },
                    selectedChip = SampleDestinations.categoryLabel(selectedCategory),
                    onChipSelected = { label ->
                        val selectedId = categoryOptions.firstOrNull {
                            SampleDestinations.categoryLabel(it) == label
                        } ?: SampleDestinations.ALL_CATEGORIES
                        selectedCategory = selectedId
                    }
                )
            }

            if (filteredDestinations.isEmpty()) {
                item {
                    Text(
                        text = "No hay destinos que coincidan con la busqueda.",
                        color = colorResource(R.color.dark_beige),
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    )
                }
            } else {
                items(filteredDestinations.size) { index ->
                    SearchResultRow(
                        point = filteredDestinations[index],
                        onClick = {
                            navController.navigate(
                                "${Screen.RouteOverview.name}?destinationId=${filteredDestinations[index].id}"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    point: MapPoint,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .border(1.dp, colorResource(R.color.red_50), RoundedCornerShape(15.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = point.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )
            Text(
                text = point.description,
                color = colorResource(R.color.dark_beige),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Text(
            text = "Ver ruta",
            color = colorResource(R.color.red_400),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

