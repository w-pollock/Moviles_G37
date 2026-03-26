package com.example.moviles_g37.ui.screens

import androidx.lifecycle.ViewModel
import com.example.moviles_g37.analytics.AppAnalytics

class LocationDetailsViewModel : ViewModel() {
    init {
        AppAnalytics.track("screen_view", mapOf("screen_name" to "location_details"))
        AppAnalytics.track("place_viewed", mapOf(
            "source_screen" to "location_details",
            "place_name" to "ML-603"
        ))
    }
}