package com.example.dmuber

data class Review(
    val userId: String? = null,  // Ensure default values for nullable compatibility
    val rating: Float = 0f,      // Default value for float
    val comments: String = ""
)