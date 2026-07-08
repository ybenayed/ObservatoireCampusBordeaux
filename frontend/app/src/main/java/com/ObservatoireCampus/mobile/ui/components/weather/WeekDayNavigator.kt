package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Fleches EN HAUT pour naviguer de jour en jour (gauche = -1, droite = +1),
 * limitees a la fenetre disponible cote backend (-7j / +7j environ).
 */
@Composable
fun WeekDayNavigator(
    selectedDate: LocalDate,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate = LocalDate.now().minusDays(7),
    maxDate: LocalDate = LocalDate.now().plusDays(6)
) {
    val label = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH)
        .replaceFirstChar { it.uppercase() } + " " +
            selectedDate.format(DateTimeFormatter.ofPattern("d MMMM", Locale.FRENCH))

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPrevious, enabled = !selectedDate.isBefore(minDate.plusDays(1))) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Jour precedent", tint = Color.White)
        }
        Text(text = label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        IconButton(onClick = onNext, enabled = !selectedDate.isAfter(maxDate.minusDays(1))) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Jour suivant", tint = Color.White)
        }
    }
}