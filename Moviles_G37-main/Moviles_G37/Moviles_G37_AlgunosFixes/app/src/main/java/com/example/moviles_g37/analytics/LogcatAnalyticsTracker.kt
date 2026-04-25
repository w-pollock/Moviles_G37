package com.example.moviles_g37.analytics

import android.util.Log

class LogcatAnalyticsTracker : AnalyticsTracker {
    override fun track(event: String, params: Map<String, Any?>) {
        Log.d("AnalyticsTracker", "event=$event params=$params")
    }
}