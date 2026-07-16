package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun CampusButton(
    languageViewModel: LanguageViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var translatedDesc by remember { mutableStateOf("Campus") }

    LaunchedEffect(currentLanguage) {
        translatedDesc = languageViewModel.translate("Campus")
    }

    Surface(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.LocationCity,
                contentDescription = translatedDesc,
                tint = ObcampusPrimary
            )
        }
    }
}