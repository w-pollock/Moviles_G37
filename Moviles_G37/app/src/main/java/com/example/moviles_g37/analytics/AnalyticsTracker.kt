package com.example.moviles_g37.analytics

interface AnalyticsTracker {
    fun track(event: String, params: Map<String, Any?> = emptyMap())
}