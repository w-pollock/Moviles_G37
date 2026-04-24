package com.example.moviles_g37.data

import com.google.android.gms.maps.model.LatLng


data class PlaceInfo(
    val id: String,
    val name: String,
    val category: String,   // "building" | "classroom" | "study" | "food" | "restroom" | "sports" | "service"
    val latitude: Double,
    val longitude: Double,
    val building: String,   // por ejemplo. "ML", "SD", "O"
    val floor: String? = null,
    val description: String = "",
    val nearbyPois: List<String> = emptyList()
) {
    val position: LatLng get() = LatLng(latitude, longitude)

    fun matchesQuery(query: String): Boolean {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return false
        return name.lowercase().contains(q) ||
                id.lowercase().contains(q) ||
                building.lowercase().contains(q) ||
                category.lowercase().contains(q) ||
                description.lowercase().contains(q)
    }
}
