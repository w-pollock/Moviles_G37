package com.example.moviles_g37.analytics

interface AnalyticsObserver {
    fun onEventTracked(event: AnalyticsEvent)
}

object AppAnalytics {

    private val tracker: AnalyticsTracker = LogcatAnalyticsTracker()
    private val observers = mutableListOf<AnalyticsObserver>()
    private val _eventLog = mutableListOf<AnalyticsEvent>()

    val eventLog: List<AnalyticsEvent> get() = _eventLog.toList()

    fun addObserver(observer: AnalyticsObserver){
        if (!observers.contains(observer)){
            observers.add(observer)
        }
    }

    fun removeObserver(observer: AnalyticsObserver){
        observers.remove(observer)
    }

    fun track(event: String, params: Map<String, Any?> = emptyMap()){
        tracker.track(event, params)
        val analyticsEvent = AnalyticsEvent(name = event, params = params)
        _eventLog.add(analyticsEvent)
        observers.forEach { it.onEventTracked(analyticsEvent) }
    }
}