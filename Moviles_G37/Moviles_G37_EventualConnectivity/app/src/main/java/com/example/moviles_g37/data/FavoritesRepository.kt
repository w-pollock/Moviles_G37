package com.example.moviles_g37.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Simple favorite place record. Lives in the data layer so it can be shared
 * between FavoritesScreen and the star button on LocationDetailsScreen.
 */
data class FavoritePlace(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String
) {
    companion object {
        fun fromPlace(place: PlaceInfo): FavoritePlace = FavoritePlace(
            id = place.id,
            title = place.name,
            subtitle = place.description.ifBlank { place.building },
            category = categoryFromPlace(place.category)
        )

        /** Maps raw place categories onto the user-facing Favorites filter chips. */
        private fun categoryFromPlace(raw: String): String = when (raw.lowercase()) {
            "food" -> "Food"
            "study", "classroom" -> "Academic"
            "sports" -> "Sports"
            else -> "Academic"
        }
    }
}

class FavoritesRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userId(): String = auth.currentUser?.uid ?: "anonymous"

    suspend fun getFavorites(): List<FavoritePlace> {
        return try {
            val snapshot = db.collection("users")
                .document(userId())
                .collection("favorites")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                FavoritePlace(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    subtitle = doc.getString("subtitle") ?: "",
                    category = doc.getString("category") ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error getting favorites: ${e.message}")
            emptyList()
        }
    }

    suspend fun isFavorite(placeId: String): Boolean {
        if (placeId.isBlank()) return false
        return try {
            val doc = db.collection("users")
                .document(userId())
                .collection("favorites")
                .document(placeId)
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error checking favorite: ${e.message}")
            false
        }
    }

    suspend fun addFavorite(place: FavoritePlace) {
        try {
            val data = hashMapOf(
                "title" to place.title,
                "subtitle" to place.subtitle,
                "category" to place.category
            )
            db.collection("users")
                .document(userId())
                .collection("favorites")
                .document(place.id)
                .set(data)
                .await()
            Log.d("FavoritesRepository", "Favorite saved: ${place.title}")
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error saving favorite: ${e.message}")
        }
    }

    suspend fun removeFavorite(placeId: String) {
        try {
            db.collection("users")
                .document(userId())
                .collection("favorites")
                .document(placeId)
                .delete()
                .await()
            Log.d("FavoritesRepository", "Favorite removed: $placeId")
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Error removing favorite: ${e.message}")
        }
    }
}
