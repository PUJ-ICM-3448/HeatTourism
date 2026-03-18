package com.naranjapina.heat_tourism.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R

data class StatRowItemData(
    val title: String,
    val value: Any,
)

@Composable
fun RowScope.StatsRowItemComp(item: StatRowItemData) {
    Column(
        modifier = Modifier
            .weight(1f)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            )
            .border(
                width = 1.dp,
                color = colorResource(R.color.red_50)
            )
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.value.toString(),
            color = colorResource(R.color.red_500),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = item.title,
            color = colorResource(R.color.neutral_600),
            fontSize = 14.sp
        )
    }
}

@Composable
fun StatsRow(modifier: Modifier = Modifier, items: List<StatRowItemData>) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items.forEach { item ->
            StatsRowItemComp(item)
        }
    }
}