package com.tejas.playing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Third : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        // Get references to UI elements
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val tvLoginRedirect = findViewById<TextView>(R.id.tvLoginRedirect)

        // Handle Login redirect text click
        tvLoginRedirect.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        // Handle Sign Up button click
        btnSignUp.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()

            // Validate user input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@Third, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a RegisterRequest object
            val request = RegisterRequest(email, password)

            // Make API call using Retrofit
            RetrofitClient.instance.registerUser(request).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        // Registration was successful
                        Toast.makeText(this@Third, response.body()?.message, Toast.LENGTH_SHORT).show()

                        // Redirect to Home activity
                        val intent = Intent(this@Third, Home::class.java)
                        intent.putExtra("user_email", email) // Optional: Pass user email to Home activity
                        startActivity(intent)

                        // Finish Third activity so user cannot return back to it
                        finish()
                    } else {
                        // If registration failed, show a message
                        Toast.makeText(this@Third, "Registration failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // Handle failure in the API call
                    Toast.makeText(this@Third, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
