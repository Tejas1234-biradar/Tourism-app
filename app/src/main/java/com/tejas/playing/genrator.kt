package com.tejas.playing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tejas.playing.databinding.ActivityGenratorBinding

class genrator : AppCompatActivity() {
    private lateinit var binding: ActivityGenratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenratorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val itineraryJson = intent.getStringExtra("itinerary")

        if (!itineraryJson.isNullOrEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<Map<String, List<String>>>() {}.type
            val itineraryMap: Map<String, List<String>> = gson.fromJson(itineraryJson, type)

            updateItineraryViews(itineraryMap)
        } else {
            binding.textView6.text = "No itinerary received"
        }
    }

    private fun updateItineraryViews(itinerary: Map<String, List<String>>) {
        // List of TextViews for Days (Headings)
        val dayTextViews = listOf(
            binding.textView6, binding.textView7, binding.textView8,
            binding.textView9
        )

        // List of TextViews for Activities (Under each Day)
        val activitiesTextViews = listOf(
            binding.activitiesTextView3,
            binding.activitiesTextView4, binding.activitiesTextView5, binding.activitiesTextView6
        )

        var index = 0
        for ((day, activities) in itinerary) {
            if (index < dayTextViews.size) {
                dayTextViews[index].text = day // Set the day heading
                activitiesTextViews[index].text = formatActivities(activities) // Set the activities for the day
                index++
            } else {
                break // Stop if there are more days than available TextViews
            }
        }
    }

    private fun formatActivities(activities: List<String>): String {
        return activities.joinToString("\n - ", prefix = "Activities:\n - ")
    }
}
