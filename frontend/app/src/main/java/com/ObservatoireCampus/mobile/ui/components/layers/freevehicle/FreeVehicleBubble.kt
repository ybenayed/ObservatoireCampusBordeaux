package com.ObservatoireCampus.mobile.ui.components.freevehicle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.ui.components.layers.freevehicle.FreeVehicleTypeStyle
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehicleDetailDto

@Composable
fun FreeVehicleBubble(
    detail: FreeVehicleDetailDto?,
    loading: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.widthIn(max = 340.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {

            // EN-TÊTE : Design centré style TB avec la couleur Mauve
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VÉHICULE EN LIBRE-SERVICE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF7B1FA2), // Joli Mauve/Deep Purple pour le libre-service
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detail?.let { FreeVehicleTypeStyle.label(it.vehicleTypeId) } ?: "Véhicule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Séparateur horizontal identique à TB
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)

            // CORPS : Contenu épuré (sans identifiant ni tarif)
            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                    }
                }
                detail == null -> {
                    Text(
                        text = "Informations indisponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        // Couleurs d'état pour le statut
                        val (statusText, statusColor) = when {
                            detail.isDisabled == true -> "Hors service" to Color(0xFFD32F2F)  // Rouge
                            detail.isReserved == true -> "Réservé" to Color(0xFFFBC02D)       // Jaune/Ambre
                            else -> "Disponible" to Color(0xFF4CAF50)                         // Vert
                        }

                        InfoRow(
                            label = "Disponibilité",
                            value = statusText,
                            valueColor = statusColor
                        )

                        // Autonomie
                        detail.currentRangeMeters?.let {
                            InfoRow(
                                label = "Autonomie",
                                value = "${it / 1000} km",
                                valueColor = Color(0xFF1976D2) // Bleu
                            )
                        }

                        // Dernière mise à jour
                        detail.lastReported?.let {
                            InfoRow(
                                label = "Dernière position",
                                value = formatLastReported(it),
                                valueColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
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