package com.example.moviles_g37.data

import android.util.Log
import com.example.moviles_g37.ui.screens.FavoritePlace
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoritesRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: "anonymous"
    }

    suspend fun getFavorites(): List<FavoritePlace> {
        return try {
            val userId = getUserId()
            val snapshot = db
                .collection("users")
                .document(userId)
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
        } catch (e: Exception){
            Log.e("FavoritesRepository", "Error getting favorites: ${e.message}")
            emptyList()
        }
    }

    suspend fun addFavorite(place: FavoritePlace){
        try {
            val userId = getUserId()
            val placeData = hashMapOf(
                "title" to place.title,
                "subtitle" to place.subtitle,
                "category" to place.category
            )
            db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(place.id)
                .set(placeData)
                .await()
            Log.d("FavoritesRepository", "Favorite saved: ${place.title}")
        } catch (e: Exception){
            Log.e("FavoritesRepository", "Error saving favorite: ${e.message}")
        }
    }

    suspend fun removeFavorite(placeId: String){
        try {
            val userId = getUserId()
            db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(placeId)
                .delete()
                .await()
            Log.d("FavoritesRepository", "Favorite removed: $placeId")
        } catch (e: Exception){
            Log.e("FavoritesRepository", "Error removing favorite: ${e.message}")
        }
    }
}