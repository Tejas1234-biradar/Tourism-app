package com.tejas.playing

data class ApiResponse(
    val status: String,  // This field will indicate success or failure (e.g., "success" or "error")
    val token: String?,  // This will store the authentication token (nullable)
    val message: String  // This will store the response message, e.g., "Login successful" or "Invalid credentials"
)
