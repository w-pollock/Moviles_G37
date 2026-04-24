package com.example.moviles_g37.data

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PlacesRepository(private val context: Context? = null) {

    private val db = FirebaseFirestore.getInstance()
    private val localDao: LocalPlacesDao? = context?.let { LocalPlacesDao(it) }

    suspend fun getPlaces(): List<PlaceInfo> {
        val online = context?.let { ConnectivityObserver.isConnected(it) } ?: true

        if (!online) {
            localDao?.loadPlaces()?.let {
                Log.d("PlacesRepository", "Offline → returning ${it.size} cached places")
                return it
            }
            Log.d("PlacesRepository", "Offline & no local cache → static seed")
            return SenecaPlaces.all
        }

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
            localDao?.savePlaces(result)
            result
        } catch (e: Exception) {
            Log.e("PlacesRepository", "Error getting places: ${e.message}")
            localDao?.loadPlaces() ?: SenecaPlaces.all
        }
    }

    suspend fun getPlaceById(id: String): PlaceInfo? {
        if (id.isBlank()) return null
        val online = context?.let { ConnectivityObserver.isConnected(it) } ?: true

        if (!online) {
            return localDao?.loadPlaces()?.firstOrNull { it.id == id }
                ?: SenecaPlaces.all.firstOrNull { it.id == id }
        }

        return try {
            val doc = db.collection("places").document(id).get().await()
            if (doc.exists()) parsePlace(doc.id, doc.data)
                ?: SenecaPlaces.all.firstOrNull { it.id == id }
            else SenecaPlaces.all.firstOrNull { it.id == id }
        } catch (e: Exception) {
            Log.e("PlacesRepository", "Error getting place $id: ${e.message}")
            localDao?.loadPlaces()?.firstOrNull { it.id == id }
                ?: SenecaPlaces.all.firstOrNull { it.id == id }
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
