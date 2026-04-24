package com.example.moviles_g37.analytics

data class AnalyticsEvent(
    val name: String,
    val params: Map<String, Any?>,
    val timestamp: Long = System.currentTimeMillis()
)