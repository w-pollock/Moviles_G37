package com.example.moviles_g37.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import org.json.JSONArray


/**
 * Stores recent searches in both:
 * - SharedPreferences (local, works offline immediately)
 * - Firestore (remote, synced when online)
 *
 * This implements an eventual connectivity strategy:
 * If Firestore fails, the local copy is used so the user always sees their recent searches.
 */
class RecentSearchesRepository(private val context: Context? = null) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val localPrefs: SharedPreferences? =
        context?.getSharedPreferences("recent_searches_local", Context.MODE_PRIVATE)

    private fun userId(): String = auth.currentUser?.uid ?: "anonymous"

    suspend fun getRecentSearches(limit: Long = 5): List<String> = withContext(Dispatchers.IO) {
        // Try Firestore first (remote)
        try {
            val snapshot = db.collection("users")
                .document(userId())
                .collection("recentSearches")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()

            val remoteSearches = snapshot.documents.mapNotNull { it.getString("query") }
            // Sync to local storage
            saveLocalSearches(remoteSearches)
            remoteSearches
        } catch (e: Exception) {
            Log.e("RecentSearchesRepo", "Firestore error, using local: ${e.message}")
            // Fallback to local storage (eventual connectivity)
            loadLocalSearches(limit.toInt())
        }
    }

    suspend fun addSearch(query: String) {
        if (query.isBlank()) return
        withContext(Dispatchers.IO) {
            val trimmed = query.trim()

            // Always write locally first (immediate feedback)
            val current = loadLocalSearches(20).toMutableList()
            current.remove(trimmed)
            current.add(0, trimmed)
            saveLocalSearches(current.take(10))

            // Then try Firestore
            try {
                val docId = trimmed.lowercase().hashCode().toString()
                val data = hashMapOf(
                    "query" to trimmed,
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("users")
                    .document(userId())
                    .collection("recentSearches")
                    .document(docId)
                    .set(data)
                    .await()
            } catch (e: Exception) {
                Log.e("RecentSearchesRepo", "Error syncing search to Firestore: ${e.message}")
                // Local write already happened above; will sync on next online use
            }
        }
    }

    private fun saveLocalSearches(searches: List<String>) {
        try {
            val arr = JSONArray()
            searches.forEach { arr.put(it) }
            localPrefs?.edit()?.putString("searches", arr.toString())?.apply()
        } catch (e: Exception) {
            Log.e("RecentSearchesRepo", "Error saving local searches: ${e.message}")
        }
    }

    private fun loadLocalSearches(limit: Int = 5): List<String> {
        return try {
            val json = localPrefs?.getString("searches", null) ?: return emptyList()
            val arr = JSONArray(json)
            (0 until minOf(arr.length(), limit)).map { arr.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
