package com.tejas.playing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.tejas.playing.api.ItineraryRequest
import com.tejas.playing.api.ItineraryResponse
import com.tejas.playing.RetrofitClient
import com.tejas.playing.databinding.ActivityItiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Iti : AppCompatActivity() {
    private lateinit var binding: ActivityItiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.btnGenerate.setOnClickListener {
            sendItineraryRequest()
        }
    }

    private fun sendItineraryRequest() {
        val budget = binding.inputBudget.text.toString()
        val destination = binding.inputDestination.text.toString()
        val numPeople = binding.inputPeople.text.toString().toIntOrNull() ?: 1
        val numDays = binding.inputDays.text.toString().toIntOrNull() ?: 1
        val dateOfTrip = binding.inputDate.text.toString()
        val tripType = binding.inputTripType.text.toString()

        val request = ItineraryRequest(budget, destination, numPeople, numDays, dateOfTrip, tripType)

        val apiService = RetrofitClient.instance
        apiService.generateItinerary(request).enqueue(object : Callback<ItineraryResponse> {
            override fun onResponse(call: Call<ItineraryResponse>, response: Response<ItineraryResponse>) {
                if (response.isSuccessful) {
                    val itinerary = response.body()?.itinerary ?: emptyMap()

                    // Convert itinerary Map to JSON string
                    val gson = Gson()
                    val itineraryJson = gson.toJson(itinerary)

                    Log.d("API_SUCCESS", "Itinerary JSON: $itineraryJson") // Debugging Log

                    val intent = Intent(this@Iti, genrator::class.java)
                    intent.putExtra("itinerary", itineraryJson)
                    startActivity(intent)
                } else {
                    Log.e("API_ERROR", "Failed: ${response.errorBody()?.string()}") // Debugging Log
                    Toast.makeText(this@Iti, "Failed to generate itinerary", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ItineraryResponse>, t: Throwable) {
                Toast.makeText(this@Iti, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("API_ERROR", "Error: ${t.message}")
            }
        })
    }
}
