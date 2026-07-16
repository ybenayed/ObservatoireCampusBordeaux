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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.CampusDto
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun CampusListPanel(
    campusList: List<CampusDto>,
    languageViewModel: LanguageViewModel,
    onCampusSelected: (CampusDto) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var translatedTitle by remember { mutableStateOf("Campus") }

    LaunchedEffect(currentLanguage) {
        translatedTitle = languageViewModel.translate("Campus")
    }

    Surface(
        modifier = modifier.widthIn(min = 200.dp, max = 260.dp),
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            Text(text = translatedTitle, color = ObcampusPrimary, modifier = Modifier.padding(12.dp))
            Divider()

            LazyColumn {
                items(campusList) { campus ->
                    // Traduction dynamique à la volée du nom du campus
                    var translatedCampusName by remember(campus.name, currentLanguage) {
                        mutableStateOf(campus.name)
                    }
                    LaunchedEffect(campus.name, currentLanguage) {
                        translatedCampusName = languageViewModel.translate(campus.name)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCampusSelected(campus) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = ObcampusPrimary)
                        Text(text = translatedCampusName, modifier = Modifier.padding(start = 8.dp))
                    }
                    Divider()
                }
            }
        }
    }
}