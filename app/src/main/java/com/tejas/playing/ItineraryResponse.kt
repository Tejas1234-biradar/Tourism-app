package com.tejas.playing.api

data class ItineraryResponse(
    val itinerary: Map<String, List<String>> // Key: Day, Value: List of activities
)
