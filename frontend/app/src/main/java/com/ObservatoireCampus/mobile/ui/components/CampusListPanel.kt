package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.CampusDto
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary

/**
 * Petite liste qui s'ouvre au clic sur CampusButton, puisqu'il y a
 * plusieurs campus (un simple bouton ne peut pas savoir lequel choisir).
 * Cliquer sur un campus déclenche le zoom automatique (fonctionnel).
 */
@Composable
fun CampusListPanel(
    campusList: List<CampusDto>,
    onCampusSelected: (CampusDto) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.widthIn(min = 200.dp, max = 260.dp),
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            Text(text = "Campus", color = ObcampusPrimary, modifier = Modifier.padding(12.dp))
            Divider()

            LazyColumn {
                items(campusList) { campus ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCampusSelected(campus) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = ObcampusPrimary)
                        Text(text = campus.name, modifier = Modifier.padding(start = 8.dp))
                    }
                    Divider()
                }
            }
        }
    }
}