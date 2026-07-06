package com.ObservatoireCampus.mobile.ui.components.station

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.station.PassageDto
import com.ObservatoireCampus.mobile.model.station.StationTBPositionDto

@Composable
fun StationTBBubble(
    station: StationTBPositionDto,
    passages: List<PassageDto>,
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
                Column {
                    Text(station.nom, style = MaterialTheme.typography.titleMedium)
                    Text(station.mode, style = MaterialTheme.typography.labelSmall)
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(Modifier.height(8.dp))

            when {
                loading -> CircularProgressIndicator(modifier = Modifier.size(20.dp))
                passages.isEmpty() -> Text("Aucun passage prevu", style = MaterialTheme.typography.bodySmall)
                else -> passages.take(5).forEach { p ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${p.ligne} → ${p.destination ?: "?"}", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = if (p.retardSecondes != null && p.retardSecondes > 60)
                                "+${p.retardSecondes / 60} min" else "A l'heure",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}