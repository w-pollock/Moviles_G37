package com.example.moviles_g37.analytics

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseAnalyticsTracker: AnalyticsTracker {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun track(event: String, params: Map<String, Any?>) {
        val userId = auth.currentUser?.uid ?: "anonymous"

        val eventData = hashMapOf<String, Any?>(
            "event_name" to event,
            "user_id" to userId,
            "timestamp" to System.currentTimeMillis()
        )
        eventData.putAll(params)

        db.collection("analytics_events")
            .add(eventData)
            .addOnSuccessListener {
                Log.d("Analytics", "EVENT SAVED: $event | PARAMS: $params")

            }
            .addOnFailureListener { e ->
                Log.e("Analytics", "ERROR saving event: $event | ${e.message}")
            }
    }
}