package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

/**
 * Barre de recherche avec placeholder et contenu d'accessibilité traduits dynamiquement.
 */
@Composable
fun SearchBar(
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    var translatedPlaceholder by remember { mutableStateOf("Rechercher un lieu...") }
    var translatedSearchDesc by remember { mutableStateOf("Rechercher") }

    LaunchedEffect(currentLanguage) {
        translatedPlaceholder = languageViewModel.translate("Rechercher un lieu...")
        translatedSearchDesc = languageViewModel.translate("Rechercher")
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 3.dp,
        color = androidx.compose.ui.graphics.Color.White
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onSearch(it)
            },
            placeholder = { Text(translatedPlaceholder) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = translatedSearchDesc)
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ObcampusPrimary,
                cursorColor = ObcampusPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}