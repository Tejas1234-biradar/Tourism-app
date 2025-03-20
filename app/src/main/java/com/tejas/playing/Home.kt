package com.tejas.playing

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home) // Ensure this is correct

        // Get references to image views (the images for itinerary and translator)
        val itineraryImageView = findViewById<ImageView>(R.id.imageView2) // Update ID as per your layout
        val translatorImageView = findViewById<ImageView>(R.id.imageView3) // Update ID as per your layout

        // Set OnClickListener for the Itinerary Generator Image
        itineraryImageView.setOnClickListener {
            // Navigate to Itinerary Activity (Iti.kt)
            val intent = Intent(this, Iti::class.java)
            startActivity(intent)
        }

        // Set OnClickListener for the Translator Image
        translatorImageView.setOnClickListener {
            // Navigate to Translator Activity (ActivityTranslator.kt)
            val intent = Intent(this, TranslatorActivity::class.java)
            startActivity(intent)
        }
    }
}
