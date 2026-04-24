package com.example.moviles_g37.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await


class RecentSearchesRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userId(): String = auth.currentUser?.uid ?: "anonymous"

    suspend fun getRecentSearches(limit: Long = 5): List<String> {
        return try {
            val snapshot = db.collection("users")
                .document(userId())
                .collection("recentSearches")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.getString("query") }
        } catch (e: Exception) {
            Log.e("RecentSearchesRepo", "Error getting recent searches: ${e.message}")
            emptyList()
        }
    }


    suspend fun addSearch(query: String) {
        if (query.isBlank()) return
        try {
            val trimmed = query.trim()
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
            Log.e("RecentSearchesRepo", "Error adding search: ${e.message}")
        }
    }
}
