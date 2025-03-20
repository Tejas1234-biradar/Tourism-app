package com.tejas.playing

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.tejas.playing.api.LoginRequest
import com.tejas.playing.RetrofitClient
import com.tejas.playing.ApiResponse

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val loginButton: Button = findViewById(R.id.btnLogin)
        val signUpText: TextView = findViewById(R.id.tvSignUp)

        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.etUsername).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(email, password)

            RetrofitClient.instance.loginUser(request).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        // Debugging log to check the actual response
                        Log.d("API Response", "Raw Response: $responseBody")

                        if (responseBody != null ) {
                            saveToken(responseBody.token)

                            Toast.makeText(this@SecondActivity, "Login successful!", Toast.LENGTH_SHORT).show()

                            // Redirect to Home Activity
                            startActivity(Intent(this@SecondActivity, Home::class.java))
                            finish() // Prevent back navigation to login screen
//                        } else {
//                            Toast.makeText(
//                                this@SecondActivity,
//                                "Login failed: ${responseBody?.message ?: "Unknown error"}",
//                                Toast.LENGTH_SHORT
//                            ).show()
                        }
                    } else {
                        Toast.makeText(this@SecondActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.e("LoginError", "Error: ${t.message}", t)
                    Toast.makeText(this@SecondActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        signUpText.setOnClickListener {
            startActivity(Intent(this, Third::class.java))
        }
    }

    private fun saveToken(token: String?) {
        if (token != null) {
            val sharedPref: SharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("user_token", token)
            editor.apply()
        }
    }
}
