package com.example.moviles_g37.data

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await


class PlacesRepository(private val context: Context? = null) {

    private val db = FirebaseFirestore.getInstance()
    private val localDao: LocalPlacesDao? = context?.let { LocalPlacesDao(it) }

    /**
     * Returns places using a 3-tier strategy:
     * 1. In-memory LRU cache (fast, ephemeral)
     * 2. SharedPreferences local storage (persists across app restarts)
     * 3. Firestore remote (requires internet)
     * Falls back to SenecaPlaces seed data if all else fails.
     */
    suspend fun getPlaces(): List<PlaceInfo> = withContext(Dispatchers.IO) {
        // Tier 1: In-memory cache
        CacheManager.getPlaces()?.let { return@withContext it }

        // Tier 2: Local storage (eventual connectivity – offline support)
        localDao?.loadPlaces()?.let { cached ->
            CacheManager.putPlaces(cached)
            // Trigger async background refresh from Firestore if online
            return@withContext cached
        }

        // Tier 3: Firestore
        fetchFromFirestore()
    }

    private suspend fun fetchFromFirestore(): List<PlaceInfo> {
        return try {
            val snapshot = db.collection("places").get().await()
            val places = snapshot.documents.mapNotNull { parsePlace(it.id, it.data) }
            val result = if (places.isEmpty()) {
                Log.d("PlacesRepository", "No places in Firestore, returning seed fallback")
                SenecaPlaces.all
            } else {
                Log.d("PlacesRepository", "Loaded ${places.size} places from Firestore")
                places
            }
            // Persist to local storage and in-memory cache
            CacheManager.putPlaces(result)
            localDao?.savePlaces(result)
            result
        } catch (e: Exception) {
            Log.e("PlacesRepository", "Error getting places: ${e.message}")
            // Fallback to seed data
            val fallback = SenecaPlaces.all
            CacheManager.putPlaces(fallback)
            fallback
        }
    }

    suspend fun getPlaceById(id: String): PlaceInfo? = withContext(Dispatchers.IO) {
        if (id.isBlank()) return@withContext null

        // Check in-memory cache first
        CacheManager.getPlaceById(id)?.let { return@withContext it }

        // Check full places list (will also populate cache)
        val all = getPlaces()
        all.firstOrNull { it.id == id } ?: try {
            val doc = db.collection("places").document(id).get().await()
            if (doc.exists()) parsePlace(doc.id, doc.data) else null
        } catch (e: Exception) {
            Log.e("PlacesRepository", "Error getting place $id: ${e.message}")
            SenecaPlaces.all.firstOrNull { it.id == id }
        }
    }

    suspend fun searchPlaces(query: String): List<PlaceInfo> {
        if (query.isBlank()) return emptyList()
        val all = getPlaces()
        return all.filter { it.matchesQuery(query) }
    }

    suspend fun getPlacesByCategory(category: String): List<PlaceInfo> {
        val all = getPlaces()
        return all.filter { it.category.equals(category, ignoreCase = true) }
    }

    suspend fun seedIfEmpty() {
        try {
            val snapshot = db.collection("places").limit(1).get().await()
            if (!snapshot.isEmpty) {
                Log.d("PlacesRepository", "Seed skipped — places collection already populated")
                return
            }
            Log.d("PlacesRepository", "Seeding ${SenecaPlaces.all.size} places into Firestore")
            SenecaPlaces.all.forEach { p ->
                val data = hashMapOf<String, Any?>(
                    "name" to p.name,
                    "category" to p.category,
                    "latitude" to p.latitude,
                    "longitude" to p.longitude,
                    "building" to p.building,
                    "floor" to p.floor,
                    "description" to p.description,
                    "nearbyPois" to p.nearbyPois
                )
                db.collection("places").document(p.id).set(data).await()
            }
            Log.d("PlacesRepository", "Seed complete")
            CacheManager.invalidate()
        } catch (e: Exception) {
            Log.e("PlacesRepository", "Error seeding places: ${e.message}")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parsePlace(id: String, data: Map<String, Any?>?): PlaceInfo? {
        if (data == null) return null
        val lat = (data["latitude"] as? Number)?.toDouble() ?: return null
        val lng = (data["longitude"] as? Number)?.toDouble() ?: return null
        return PlaceInfo(
            id = id,
            name = data["name"] as? String ?: "",
            category = data["category"] as? String ?: "building",
            latitude = lat,
            longitude = lng,
            building = data["building"] as? String ?: "",
            floor = data["floor"] as? String,
            description = data["description"] as? String ?: "",
            nearbyPois = (data["nearbyPois"] as? List<String>) ?: emptyList()
        )
    }
}
