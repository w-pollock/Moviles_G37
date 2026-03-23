package com.example.moviles_g37.analytics

object AppAnalytics {
    private val tracker: AnalyticsTracker = LogcatAnalyticsTracker()

    fun track(event: String, params: Map<String, Any?> = emptyMap()) {
        tracker.track(event, params)
    }
}