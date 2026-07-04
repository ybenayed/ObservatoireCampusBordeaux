package com.ObservatoireCampus.mobile.model.layers

// Etat generique d'un item de sous-liste dans un layer (Parking, Velo, Bus...)
data class LayerItemUiState(
        val key: String,       // identifiant technique (ex: taType pour Parking)
        val label: String,     // texte affiche (brut pour l'instant)
        val count: Long,
        val visible: Boolean = false
)