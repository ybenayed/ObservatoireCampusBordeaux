package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Affiche la date et l'heure REELLES (horloge systeme), rafraichies chaque minute.
 * Independant de la date "consultee" via les fleches (WeekDayNavigator) : sert de repere.
 */
@Composable
fun CurrentDateTimeHeader(modifier: Modifier = Modifier) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(60_000L)
        }
    }

    val dayLabel = now.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH)
        .replaceFirstChar { it.uppercase() }
    val dateFormatted = now.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH))
    val timeFormatted = now.format(DateTimeFormatter.ofPattern("HH:mm"))

    Text(
        text = "$dayLabel $dateFormatted · $timeFormatted",
        modifier = modifier.padding(vertical = 4.dp),
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    )
}