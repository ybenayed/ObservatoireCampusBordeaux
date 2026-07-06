package com.ObservatoireCampus.mobile.ui.components.freevehicle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.components.layers.freevehicle.FreeVehicleTypeStyle
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehicleDetailDto

@Composable
fun FreeVehicleBubble(
    detail: FreeVehicleDetailDto?,
    loading: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.widthIn(max = 340.dp), elevation = CardDefaults.cardElevation(6.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = detail?.let { FreeVehicleTypeStyle.label(it.vehicleTypeId) } ?: "Vehicule",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Libre-service",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(Modifier.height(10.dp))

            when {
                loading -> Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp))
                }
                detail == null -> Text("Informations indisponibles", style = MaterialTheme.typography.bodySmall)
                else -> {
                    InfoRow("Identifiant", detail.bikeId.take(13) + "...")
                    InfoRow(
                        "Disponibilite",
                        when {
                            detail.isDisabled == true -> "Hors service"
                            detail.isReserved == true -> "Reserve"
                            else -> "Disponible"
                        }
                    )
                    detail.currentRangeMeters?.let {
                        InfoRow("Autonomie", "${it / 1000} km")
                    }
                    detail.lastReported?.let {
                        InfoRow("Derniere position", formatLastReported(it))
                    }
                    detail.pricingPlanId?.let {
                        InfoRow("Tarif", it)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatLastReported(iso: String): String {
    return try {
        val instant = java.time.Instant.parse(iso)
        val now = java.time.Instant.now()
        val seconds = java.time.Duration.between(instant, now).seconds
        when {
            seconds < 60 -> "il y a ${seconds}s"
            seconds < 3600 -> "il y a ${seconds / 60} min"
            else -> "il y a ${seconds / 3600} h"
        }
    } catch (e: Exception) {
        iso
    }
}