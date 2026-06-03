package com.naranjapina.heat_tourism.core.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R

@Composable
fun LazyFilterChipRow(
    chips: List<String> = listOf("Restaurantes", "Eventos", "Tours", "Lugares historicos"),
    selectedChip: String? = null,
    onChipSelected: (String?) -> Unit = {}
) {

    LazyRow(
        modifier = Modifier.padding(10.dp, 0.dp)
    ) {
        items(chips) { chipContent ->
            FilterChip(
                modifier = Modifier.padding(5.dp),
                label = {
                    Text(
                        text = chipContent,
                        fontSize = 15.sp
                    )
                },
                onClick = {
                    if (selectedChip == chipContent) {
                        onChipSelected(null)
                    } else {
                        onChipSelected(chipContent)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    selectedContainerColor = colorResource(R.color.red_50)
                ),
                selected = selectedChip == chipContent,
                shape = RoundedCornerShape(percent = 100),
                border = BorderStroke(
                    color = colorResource(R.color.red_50),
                    width = 1.dp
                ),
            )
        }
    }
}