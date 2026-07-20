package com.ObservatoireCampus.mobile.ui.components.search

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

/**
 * Bulle affichée directement au-dessus du marqueur de recherche (comme sur Google Maps),
 * au lieu du popup gris par défaut d'osmdroid.
 * - Clic sur le marqueur -> s'affiche
 * - Reclic sur le marqueur, ou clic ailleurs sur la carte -> disparaît
 *   (le "clic ailleurs" est géré par le MapEventsOverlay ajouté dans CampusMap.kt)
 */
class SearchResultInfoWindow(mapView: MapView) : InfoWindow(buildView(mapView), mapView) {

    private val titleView: TextView = (mView as LinearLayout).getChildAt(0) as TextView
    private val subtitleView: TextView = (mView as LinearLayout).getChildAt(1) as TextView

    override fun onOpen(item: Any?) {
        val marker = item as? Marker ?: return
        titleView.text = marker.title
        val snippet = marker.snippet
        if (snippet.isNullOrBlank()) {
            subtitleView.visibility = View.GONE
        } else {
            subtitleView.visibility = View.VISIBLE
            subtitleView.text = snippet
        }
    }

    override fun onClose() {
        // Rien à nettoyer : la vue est recréée à chaque ouverture via onOpen()
    }

    companion object {
        private fun buildView(mapView: MapView): LinearLayout {
            val context = mapView.context
            val density = context.resources.displayMetrics.density

            // Renommé "bubbleBackground" (pas "background") pour éviter le conflit avec
            // la propriété View.background lors de l'apply{} ci-dessous.
            val bubbleBackground = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = 14f * density
                setStroke((1.5f * density).toInt(), Color.parseColor("#DC2626")) // rouge, cohérent avec le pin
            }

            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = bubbleBackground
                setPadding(
                    (14 * density).toInt(),
                    (10 * density).toInt(),
                    (14 * density).toInt(),
                    (10 * density).toInt()
                )
                minimumWidth = (150 * density).toInt()
            }

            val title = TextView(context).apply {
                setTextColor(Color.parseColor("#1A1A1A"))
                textSize = 14f
                setTypeface(typeface, Typeface.BOLD)
                gravity = Gravity.CENTER
                maxLines = 2
            }

            val subtitle = TextView(context).apply {
                setTextColor(Color.GRAY)
                textSize = 12f
                gravity = Gravity.CENTER
                maxLines = 2
                setPadding(0, (2 * density).toInt(), 0, 0)
            }

            container.addView(title)
            container.addView(subtitle)
            return container
        }
    }
}