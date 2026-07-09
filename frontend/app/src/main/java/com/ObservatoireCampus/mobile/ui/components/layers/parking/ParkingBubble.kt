package com.ObservatoireCampus.mobile.ui.components.parking

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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.parking.ParkingStatusDto
import com.ObservatoireCampus.mobile.ui.components.layers.parking.ParkingTypeStyle

@Composable
fun ParkingBubble(
    status: ParkingStatusDto?,
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

            // EN-TÊTE
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PARKING",
                        style = MaterialTheme.typography.labelSmall,
                        color = status?.taType?.let { ParkingTypeStyle.color(it) } ?: Color(0xFF616161),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    // Nom + sigle du type de structure à côté
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ParkingTypeStyle.structureIcon(status?.type),
                            contentDescription = ParkingTypeStyle.structureLabel(status?.type),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = status?.nom ?: "Parking",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)

            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                    }
                }
                status == null -> {
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

                        // --- Gestion de l'état ---
                        val (etatLabel, etatColor) = when (status.etat) {
                            "OUVERT" -> "Ouvert" to Color(0xFF4CAF50)
                            "COMPLET" -> "Complet" to Color(0xFFD32F2F)
                            "FERME" -> "Fermé" to Color(0xFF616161)
                            else -> "Non renseigné" to Color(0xFF9E9E9E)
                        }
                        InfoRow(label = "État", value = etatLabel, valueColor = etatColor)

                        // --- Gestion des places libres ---
                        val placesValue = if (status.libre != null) {
                            if (status.npTotal != null) "${status.libre} / ${status.npTotal}" else "${status.libre}"
                        } else {
                            "Non renseigné"
                        }

                        val placesColor = when {
                            status.libre == null -> MaterialTheme.colorScheme.onSurface
                            status.npTotal != null && status.npTotal > 0 && (status.libre.toDouble() / status.npTotal < 0.1) -> Color(0xFFD32F2F)
                            else -> Color(0xFF1976D2)
                        }

                        InfoRow(
                            label = "Places libres",
                            value = placesValue,
                            valueColor = placesColor
                        )

                        // --- Tarif / Type d'accès ---
                        InfoRow(
                            label = "Type",
                            value = status.taType?.let { ParkingTypeStyle.label(it) } ?: "Non renseigné"
                        )

                        // --- Gestionnaire ---
                        InfoRow(
                            label = "Gestionnaire",
                            value = status.exploit ?: "Non renseigné"
                        )

                        // --- Adresse ---
                        InfoRow(
                            label = "Adresse",
                            value = status.adresse ?: "Non renseigné",
                            singleLineValue = false
                        )

                        // --- Hauteur max (si dispo) ---
                        status.gabariMax?.let {
                            InfoRow(label = "Hauteur max", value = "${it}m")
                        }

                        // --- TARIFS HORAIRES (si parking payant) ---
                        val isPayant = status.taType?.uppercase()?.contains("PAYANT") == true
                        if (isPayant) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Text(
                                text = "Tarifs",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                TarifRow(label = "15 min", price = status.thQuar)
                                TarifRow(label = "30 min", price = status.thDemi)
                                TarifRow(label = "1 heure", price = status.thHeur)
                                TarifRow(label = "2 heures", price = status.th2)
                                TarifRow(label = "3 heures", price = status.th3)
                                TarifRow(label = "4 heures", price = status.th4)
                                TarifRow(label = "24 heures", price = status.th24)
                                TarifRow(label = "Nuit", price = status.thNuit)
                            }
                        }

                        // --- ALERTE SI ALIMENTATION OBSOLÈTE ---
                        if (!status.dataFraiche) {
                            Text(
                                text = "⚠ Données dynamiques obsolètes",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFD32F2F), // Rouge pour accentuer le problème
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
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
    singleLineValue: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (value == "Non renseigné") FontWeight.Normal else FontWeight.Bold,
            color = if (value == "Non renseigné") Color.Gray else valueColor,
            maxLines = if (singleLineValue) 1 else 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f).padding(start = 8.dp)
        )
    }
}