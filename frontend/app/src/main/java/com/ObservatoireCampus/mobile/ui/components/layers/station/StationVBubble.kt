package com.ObservatoireCampus.mobile.ui.components.station

import androidx.compose.foundation.background
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
import com.ObservatoireCampus.mobile.model.station.StationVDetailDto
import com.ObservatoireCampus.mobile.model.station.StationVPositionDto

@Composable
fun StationVBubble(
    position: StationVPositionDto,
    detail: StationVDetailDto?,
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

            // EN-TÊTE : Même style centré que pour la bulle de transport (TB)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "STATION VÉLO",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00897B), // Couleur Teal/Verte style Vélos en libre-service
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detail?.nom ?: position.nom,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
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

            // Même séparateur horizontal net que TB
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)

            // CORPS : Contenu dynamique avec des lignes colorées
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

                        // État de la station (En service / Hors service)
                        val enService = detail.enService ?: true
                        InfoRow(
                            label = "Statut",
                            value = if (enService) "En service" else "Hors service",
                            valueColor = if (enService) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                        )

                        // Vélos disponibles (Gros indicateur s'il en reste)
                        val velos = detail.velosDisponibles ?: 0
                        InfoRow(
                            label = "Vélos disponibles",
                            value = "$velos",
                            valueColor = if (velos > 0) Color(0xFF4CAF50) else Color(0xFFD32F2F),
                            isBold = true
                        )

                        // Sous-détails des types de vélos si disponibles
                        if (velos > 0) {
                            detail.velosClassiques?.let {
                                if (it > 0) InfoRow(label = "  • Classiques", value = "$it")
                            }
                            detail.velosElectriques?.let {
                                if (it > 0) InfoRow(label = "  • Électriques", value = "$it", valueColor = Color(0xFF1976D2))
                            }
                        }

                        // Places libres
                        val places = detail.placesDisponibles ?: 0
                        InfoRow(
                            label = "Places libres",
                            value = "$places",
                            valueColor = if (places > 0) MaterialTheme.colorScheme.onSurface else Color(0xFFD32F2F)
                        )

                        // Capacité totale
                        detail.capacite?.let {
                            InfoRow(label = "Capacité totale", value = "$it")
                        }

                        // Date de dernière mise à jour
                        detail.derniereMaj?.let {
                            InfoRow(
                                label = "Mis à jour",
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
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false
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
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
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