package com.ObservatoireCampus.mobile.ui.components.station

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.station.StationVDetailDto

@Composable
fun StationVBubble(
    detail: StationVDetailDto?,
    loading: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.widthIn(max = 320.dp), elevation = CardDefaults.cardElevation(6.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(detail?.nom ?: "Station velo", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(Modifier.height(8.dp))

            when {
                loading -> CircularProgressIndicator(modifier = Modifier.size(20.dp))
                detail == null -> Text("Donnees indisponibles", style = MaterialTheme.typography.bodySmall)
                else -> {
                    Text("${detail.velosDisponibles ?: "?"} velos disponibles", style = MaterialTheme.typography.bodySmall)
                    Text("${detail.placesDisponibles ?: "?"} places libres", style = MaterialTheme.typography.bodySmall)
                    if (detail.enService == false) {
                        Text("Station hors service", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}