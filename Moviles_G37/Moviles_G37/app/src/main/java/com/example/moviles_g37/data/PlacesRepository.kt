package com.example.moviles_g37.data

import android.util.Log
import com.example.moviles_g37.ui.screens.MapMarker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.tasks.await

class PlacesRepository{
    private val db = FirebaseFirestore.getInstance()

    private fun defaultPlaces() = listOf(
        MapMarker("ml-603", LatLng(4.6028, -74.0659), "ML-603", "Mario Laserna - Piso 6"),
        MapMarker("biblioteca", LatLng(4.6018, -74.0652), "Biblioteca ML", "Study & Research Place"),
        MapMarker("cafeteria", LatLng(4.6022, -74.0665), "Cafetería O", "Eating & Social Place"),
        MapMarker("caneca", LatLng(4.6010, -74.0648), "Centro Deportivo", "Sports & Recreation")
    )

    suspend fun getPlaces(): List<MapMarker>{
        return try {
            val snapshot = db.collection("places").get().await()
            val places = snapshot.documents.mapNotNull { doc ->
                val lat = doc.getDouble("latitude") ?: return@mapNotNull null
                val lng = doc.getDouble("longitude") ?: return@mapNotNull null
                MapMarker(
                    id = doc.id,
                    position = LatLng(lat, lng),
                    title = doc.getString("name") ?: "",
                    snippet = doc.getString("description") ?: ""
                )
            }
            if (places.isEmpty()){
                Log.d("PlacesRepository", "No places in Firestore, using defaults")
                defaultPlaces()
            } else{
                Log.d("PlacesRepository", "Loaded ${places.size} places from Firestore")
                places
            }
        } catch (e: Exception){
            Log.e("PlacesRepository", "Error getting places: ${e.message}")
            defaultPlaces()
        }
    }
}