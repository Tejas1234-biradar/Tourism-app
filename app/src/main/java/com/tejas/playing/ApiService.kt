package com.tejas.playing
import com.tejas.playing.api.LoginRequest
import retrofit2.Call
import com.tejas.playing.api.ItineraryRequest
import com.tejas.playing.api.ItineraryResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    fun registerUser(@Body request: RegisterRequest): Call<ApiResponse>

    @POST("/login")
    fun loginUser(@Body request: LoginRequest): Call<ApiResponse>

    // Add itinerary generation API
    @POST("/generate_itinerary")
    fun generateItinerary(@Body request: ItineraryRequest): Call<ItineraryResponse>
}
