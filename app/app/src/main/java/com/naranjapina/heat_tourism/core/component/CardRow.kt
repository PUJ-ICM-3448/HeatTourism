package com.naranjapina.heat_tourism.core.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naranjapina.heat_tourism.R

data class ActionCardGridItem(
    val title: String,
    val painter: Int,
    val subtitle: String?,
    val onClick: () -> Unit = {},
    val color: Int
)

@Composable
fun RowScope.ActionCardGridItemComponent(item: ActionCardGridItem) {
    val color = colorResource(item.color)

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(15.dp))
            .fillMaxHeight()
            .background(Color.White)
            .border(
                color = colorResource(R.color.red_50),
                shape = RoundedCornerShape(15.dp),
                width = 2.dp
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 100))
                .background(color.copy(alpha = .25f))
                .weight(1f)
                .aspectRatio(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(item.painter),
                colorFilter = ColorFilter.tint(
                    color
                ),
                modifier = Modifier.fillMaxSize(.6f),
                contentDescription = item.title
            )
        }
        if (item.subtitle != null)
            Text(
                text = item.subtitle,
                color = colorResource(
                    R.color.neutral_600
                )
            )
        Text(
            text = item.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CardRow(
    modifier: Modifier = Modifier,
    list: List<ActionCardGridItem>,
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        list.forEach { item ->
            ActionCardGridItemComponent(item)
        }
    }

}