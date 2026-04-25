package com.example.moviles_g37.analytics

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


/**
 * Business Questions Analytics Repository
 *
 * BQ 2:  ¿Cuáles son los salones/lugares más buscados?
 *        (top searched places by query count)
 * BQ 9:  ¿Con qué frecuencia los usuarios usan mapa vs búsqueda?
 *        (map_viewed vs search_submitted event counts)
 * BQ 10: ¿Cuáles son los lugares favoritos más guardados?
 *        (favorite_added by place_name)
 * BQ 11: ¿Cuál es el tiempo promedio de sesión del usuario?
 *        (session_started / session_ended events)
 */
object BQAnalyticsRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // BQ 11: Session tracking
    private var sessionStartTime: Long = 0L

    fun startSession() {
        sessionStartTime = System.currentTimeMillis()
        AppAnalytics.track("session_started", mapOf("timestamp" to sessionStartTime))
        Log.d("BQAnalytics", "Session started at $sessionStartTime")
    }

    fun endSession() {
        if (sessionStartTime == 0L) return
        val duration = System.currentTimeMillis() - sessionStartTime
        AppAnalytics.track(
            "session_ended",
            mapOf(
                "session_duration_ms" to duration,
                "user_id" to (auth.currentUser?.uid ?: "anonymous")
            )
        )
        Log.d("BQAnalytics", "Session ended, duration: ${duration}ms")
        sessionStartTime = 0L
    }

    /** BQ 2: Top searched places */
    suspend fun getTopSearchedPlaces(limit: Int = 5): List<Pair<String, Long>> {
        return try {
            val snapshot = db.collection("analytics_events")
                .whereEqualTo("event_name", "place_viewed")
                .whereEqualTo("source_screen", "search")
                .get().await()

            snapshot.documents
                .groupBy { it.getString("place_name") ?: "Unknown" }
                .map { (name, docs) -> name to docs.size.toLong() }
                .sortedByDescending { it.second }
                .take(limit)
        } catch (e: Exception) {
            Log.e("BQAnalytics", "BQ2 error: ${e.message}")
            // Fallback to in-memory log
            AppAnalytics.eventLog
                .filter { it.name == "place_viewed" && it.params["source_screen"] == "search" }
                .groupBy { it.params["place_name"] as? String ?: "Unknown" }
                .map { (name, events) -> name to events.size.toLong() }
                .sortedByDescending { it.second }
                .take(limit)
        }
    }

    /** BQ 9: Map usage vs Search usage frequency */
    suspend fun getMapVsSearchUsage(): Pair<Long, Long> {
        return try {
            val mapViews = db.collection("analytics_events")
                .whereEqualTo("event_name", "screen_view")
                .whereEqualTo("screen_name", "map")
                .get().await().size().toLong()

            val searchUses = db.collection("analytics_events")
                .whereEqualTo("event_name", "search_submitted")
                .get().await().size().toLong()

            mapViews to searchUses
        } catch (e: Exception) {
            Log.e("BQAnalytics", "BQ9 error: ${e.message}")
            val log = AppAnalytics.eventLog
            val mapViews = log.count { it.name == "screen_view" && it.params["screen_name"] == "map" }.toLong()
            val searches = log.count { it.name == "search_submitted" }.toLong()
            mapViews to searches
        }
    }

    /** BQ 10: Most favorited places */
    suspend fun getMostFavoritedPlaces(limit: Int = 5): List<Pair<String, Long>> {
        return try {
            val snapshot = db.collection("analytics_events")
                .whereEqualTo("event_name", "favorite_added")
                .get().await()

            snapshot.documents
                .groupBy { it.getString("place_name") ?: "Unknown" }
                .map { (name, docs) -> name to docs.size.toLong() }
                .sortedByDescending { it.second }
                .take(limit)
        } catch (e: Exception) {
            Log.e("BQAnalytics", "BQ10 error: ${e.message}")
            AppAnalytics.eventLog
                .filter { it.name == "favorite_added" }
                .groupBy { it.params["place_name"] as? String ?: "Unknown" }
                .map { (name, events) -> name to events.size.toLong() }
                .sortedByDescending { it.second }
                .take(limit)
        }
    }

    /** BQ 11: Average session duration in ms */
    suspend fun getAverageSessionDurationMs(): Long {
        return try {
            val snapshot = db.collection("analytics_events")
                .whereEqualTo("event_name", "session_ended")
                .get().await()

            val durations = snapshot.documents.mapNotNull { doc ->
                (doc.get("session_duration_ms") as? Number)?.toLong()
            }
            if (durations.isEmpty()) 0L else durations.average().toLong()
        } catch (e: Exception) {
            Log.e("BQAnalytics", "BQ11 error: ${e.message}")
            val durations = AppAnalytics.eventLog
                .filter { it.name == "session_ended" }
                .mapNotNull { (it.params["session_duration_ms"] as? Number)?.toLong() }
            if (durations.isEmpty()) 0L else durations.average().toLong()
        }
    }
}
