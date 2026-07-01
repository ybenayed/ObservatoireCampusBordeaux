package com.ObservatoireCampus.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.ObservatoireCampus.mobile.ui.screens.MapScreen  // ← AJOUT

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {      // ← remplace ObservatoireTheme
                MapScreen()
            }
        }
    }
}