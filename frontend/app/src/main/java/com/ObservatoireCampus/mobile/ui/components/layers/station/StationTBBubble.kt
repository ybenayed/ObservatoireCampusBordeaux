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
    Card(
        modifier = modifier.widthIn(max = 340.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {

            // EN-TÊTE : Type/Mode en haut, Nom de station AU MILIEU, Bouton Fermer à droite
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = station.mode.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (station.mode == "TRAM") Color(0xFF1976D2) else Color(0xFFEF6C00),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = station.nom,
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

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)

            // CORPS : Prochains passages temps réel TBM
            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                    }
                }
                passages.isEmpty() -> {
                    Text(
                        text = "Aucun passage prévu",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        passages.take(5).forEach { p ->
                            PassageRow(passage = p, mode = station.mode)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PassageRow(passage: PassageDto, mode: String) {
    val hTheorique = passage.heureTheorique?.substringBeforeLast(":") ?: "--:--"

    // Détection du retard (Si retardSecondes supérieur à 45 secondes)
    val retardSecondes = passage.retardSecondes ?: 0L
    val estEnRetard = retardSecondes > 45L

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Bloc Ligne + Destination / Direction
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        ) {


            // Nom de la direction / Destination
            Text(
                text = passage.destination ?: passage.direction ?: "Direction inconnue",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Bloc Horaires et Alertes Retards à droite
        Column(horizontalAlignment = Alignment.End) {
            // Heure théorique (en Rouge si retard, couleur classique sinon)
            Text(
                text = hTheorique,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (estEnRetard) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface
            )

            // Affichage des minutes de retard en dessous si retard détecté
            if (estEnRetard) {
                Text(
                    text = "Retard +${retardSecondes / 60} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}