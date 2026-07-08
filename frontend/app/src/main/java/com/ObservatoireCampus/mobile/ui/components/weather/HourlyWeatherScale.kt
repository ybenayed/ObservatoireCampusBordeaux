package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ObservatoireCampus.mobile.model.weather.HourlyWeatherPointDto
import kotlinx.coroutines.launch

/**
 * Echelle horaire 00:00 -> 23:00. Se positionne sur l'heure selectionnee a l'ouverture
 * (heure actuelle si "aujourd'hui"). Fleches gauche/droite pour se deplacer dans les heures.
 * Tap direct sur une heure = selection (met a jour la courbe + le panneau qualite de l'air).
 */
@Composable
fun HourlyWeatherScale(
    points: List<HourlyWeatherPointDto>,
    selectedIndex: Int,
    onHourSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            listState.scrollToItem((selectedIndex - 2).coerceAtLeast(0))
        }
    }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            scope.launch {
                listState.animateScrollToItem((listState.firstVisibleItemIndex - 3).coerceAtLeast(0))
            }
        }) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Heures precedentes", tint = Color.White)
        }

        LazyRow(
            state = listState,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(points.size) { index ->
                val point = points[index]
                val isSelected = index == selectedIndex

                Column(
                    modifier = Modifier
                        .width(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color.White.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { onHourSelected(index) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = point.time.takeLast(5), color = Color.White, fontSize = 11.sp)
                    AsyncImage(
                        model = point.icon,
                        contentDescription = point.description,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Text(
                        text = point.temperature?.let { "${it.toInt()}°" } ?: "--",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }

        IconButton(onClick = {
            scope.launch {
                val maxIndex = (points.size - 1).coerceAtLeast(0)
                listState.animateScrollToItem((listState.firstVisibleItemIndex + 3).coerceAtMost(maxIndex))
            }
        }) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Heures suivantes", tint = Color.White)
        }
    }
}