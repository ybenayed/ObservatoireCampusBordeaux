package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.model.weather.AirQualityAtDto

private data class PollutantInfo(
    val label: String,
    val icon: String,
    val unit: String,
    val explanation: String
)

// Icones "parlantes" : chacune evoque immediatement le polluant concerne.
private val PM25_INFO = PollutantInfo(
    label = "PM2.5",
    icon = "\uD83C\uDF2B\uFE0F", // brume / smog
    unit = "µg/m³",
    explanation = "Particules tres fines en suspension dans l'air, assez petites pour penetrer " +
            "profondement dans les poumons et le sang. Elles proviennent surtout de la circulation, " +
            "du chauffage et de l'industrie."
)

private val PM10_INFO = PollutantInfo(
    label = "PM10",
    icon = "\uD83D\uDCA8", // vent / poussiere en suspension
    unit = "µg/m³",
    explanation = "Particules en suspension plus grosses que les PM2.5. Elles irritent surtout " +
            "les voies respiratoires superieures (nez, gorge)."
)

private val OZONE_INFO = PollutantInfo(
    label = "Ozone (O3)",
    icon = "\u2600\uFE0F", // soleil, car l'ozone se forme sous l'effet des UV
    unit = "µg/m³",
    explanation = "Gaz forme pres du sol sous l'effet du soleil, a partir d'autres polluants. " +
            "Sa concentration est souvent plus elevee en ete et l'apres-midi, et il est irritant " +
            "pour les voies respiratoires."
)

private val NO2_INFO = PollutantInfo(
    label = "NO2",
    icon = "\uD83D\uDE97", // voiture, source principale
    unit = "µg/m³",
    explanation = "Dioxyde d'azote, emis principalement par le trafic routier et la combustion. " +
            "Il peut aggraver l'asthme et les problemes respiratoires."
)

/**
 * Panneau qualite de l'air, EN BAS de l'ecran Meteo.
 * - Par defaut (showHourly = false) : moyenne de la journee consultee (dailyData).
 * - Des qu'une heure est cliquee sur l'echelle horaire (showHourly = true) : valeurs
 *   de cette heure precise (hourData).
 * - Les polluants sont desormais affiches les uns AU-DESSUS des autres (une ligne par
 *   polluant), avec une icone parlante pour chacun. Un tap ouvre une explication.
 */
@Composable
fun AirQualityPanel(
    dailyData: AirQualityAtDto?,
    hourData: AirQualityAtDto?,
    showHourly: Boolean,
    modifier: Modifier = Modifier
) {
    val data = if (showHourly) hourData else dailyData
    var infoDialog by remember { mutableStateOf<PollutantInfo?>(null) }
    var showCategoryInfo by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (showHourly) "Qualite de l'air — a cette heure" else "Qualite de l'air — moyenne du jour",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (data == null) {
                Text(text = "Donnee indisponible", fontSize = 13.sp, color = Color.Gray)
            } else {
                // Categorie generale de l'indice (cliquable -> explication de l'indice)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showCategoryInfo = true }
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = data.icon ?: "🌍", fontSize = 30.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = data.category ?: "Inconnu", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        Text(text = data.description ?: "", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(4.dp))

                val pm25 = if (showHourly) data.pm2_5 else data.pm2_5Avg
                val pm10 = if (showHourly) data.pm10 else data.pm10Avg
                val ozone = if (showHourly) data.ozone else data.ozoneAvg
                val no2 = if (showHourly) data.nitrogenDioxide else data.nitrogenDioxideAvg

                // Polluants empiles verticalement, une ligne par polluant
                PollutantRow(PM25_INFO, pm25) { infoDialog = PM25_INFO }
                Divider()
                PollutantRow(PM10_INFO, pm10) { infoDialog = PM10_INFO }
                Divider()
                PollutantRow(OZONE_INFO, ozone) { infoDialog = OZONE_INFO }
                Divider()
                PollutantRow(NO2_INFO, no2) { infoDialog = NO2_INFO }
            }
        }
    }

    infoDialog?.let { info ->
        AlertDialog(
            onDismissRequest = { infoDialog = null },
            confirmButton = { TextButton(onClick = { infoDialog = null }) { Text("OK") } },
            title = { Text("${info.icon}  ${info.label}") },
            text = { Text(info.explanation) }
        )
    }

    if (showCategoryInfo) {
        AlertDialog(
            onDismissRequest = { showCategoryInfo = false },
            confirmButton = { TextButton(onClick = { showCategoryInfo = false }) { Text("OK") } },
            title = { Text("Indice de qualite de l'air") },
            text = {
                Text(
                    "Cet indice resume le niveau global de pollution de l'air a partir de " +
                            "plusieurs polluants (PM2.5, PM10, ozone, NO2). Plus la categorie est " +
                            "elevee, plus il est recommande de limiter les efforts physiques " +
                            "prolonges en exterieur."
                )
            }
        )
    }
}

@Composable
private fun PollutantRow(info: PollutantInfo, value: Double?, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Text(text = info.icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = info.label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value?.let { String.format("%.1f %s", it, info.unit) } ?: "--",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}