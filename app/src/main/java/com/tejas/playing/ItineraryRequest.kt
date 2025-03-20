package com.tejas.playing.api

data class ItineraryRequest(
    val budget: String,
    val destination: String,
    val numPeople: Int,
    val numDays: Int,
    val dateOfTrip: String,
    val tripType: String
)
