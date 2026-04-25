package com.example.moviles_g37.data

import android.util.LruCache
import android.util.Log


object CacheManager {

    private const val MAX_PLACES = 200
    private val placesCache = LruCache<String, List<PlaceInfo>>(1)
    private val placeByIdCache = LruCache<String, PlaceInfo>(MAX_PLACES)
    private var cacheTimestamp: Long = 0L
    private const val CACHE_TTL_MS = 5 * 60 * 1000L // 5 minutes

    fun getPlaces(): List<PlaceInfo>? {
        val now = System.currentTimeMillis()
        return if (now - cacheTimestamp < CACHE_TTL_MS) {
            placesCache["all"]?.also { Log.d("CacheManager", "Cache HIT for places list") }
        } else {
            Log.d("CacheManager", "Cache MISS/EXPIRED for places list")
            null
        }
    }

    fun putPlaces(places: List<PlaceInfo>) {
        placesCache.put("all", places)
        cacheTimestamp = System.currentTimeMillis()
        places.forEach { placeByIdCache.put(it.id, it) }
        Log.d("CacheManager", "Cached ${places.size} places")
    }

    fun getPlaceById(id: String): PlaceInfo? = placeByIdCache[id]

    fun invalidate() {
        placesCache.evictAll()
        placeByIdCache.evictAll()
        cacheTimestamp = 0L
        Log.d("CacheManager", "Cache invalidated")
    }
}
