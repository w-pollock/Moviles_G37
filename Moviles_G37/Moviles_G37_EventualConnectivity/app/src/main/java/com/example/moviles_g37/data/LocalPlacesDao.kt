package com.example.moviles_g37.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class LocalPlacesDao(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("seneca_places_cache", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PLACES = "cached_places"
        private const val KEY_TIMESTAMP = "cache_timestamp"
        private const val TTL_MS = 24 * 60 * 60 * 1000L
    }

    fun savePlaces(places: List<PlaceInfo>) {
        try {
            val arr = JSONArray()
            places.forEach { p ->
                val obj = JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("category", p.category)
                    put("latitude", p.latitude)
                    put("longitude", p.longitude)
                    put("building", p.building)
                    put("floor", p.floor ?: "")
                    put("description", p.description)
                    val poisArr = JSONArray()
                    p.nearbyPois.forEach { poisArr.put(it) }
                    put("nearbyPois", poisArr)
                }
                arr.put(obj)
            }
            prefs.edit()
                .putString(KEY_PLACES, arr.toString())
                .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                .apply()
            Log.d("LocalPlacesDao", "Saved ${places.size} places to local storage")
        } catch (e: Exception) {
            Log.e("LocalPlacesDao", "Error saving places: ${e.message}")
        }
    }

    fun loadPlaces(): List<PlaceInfo>? {
        val timestamp = prefs.getLong(KEY_TIMESTAMP, 0L)
        if (System.currentTimeMillis() - timestamp > TTL_MS) {
            Log.d("LocalPlacesDao", "Local cache expired")
            return null
        }
        val json = prefs.getString(KEY_PLACES, null) ?: return null
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                val poisArr = obj.optJSONArray("nearbyPois") ?: JSONArray()
                PlaceInfo(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    category = obj.getString("category"),
                    latitude = obj.getDouble("latitude"),
                    longitude = obj.getDouble("longitude"),
                    building = obj.getString("building"),
                    floor = obj.getString("floor").takeIf { it.isNotBlank() },
                    description = obj.optString("description"),
                    nearbyPois = (0 until poisArr.length()).map { j -> poisArr.getString(j) }
                )
            }.also { Log.d("LocalPlacesDao", "Loaded ${it.size} places from local storage") }
        } catch (e: Exception) {
            Log.e("LocalPlacesDao", "Error loading places: ${e.message}")
            null
        }
    }

    fun clear() = prefs.edit().clear().apply()
}
