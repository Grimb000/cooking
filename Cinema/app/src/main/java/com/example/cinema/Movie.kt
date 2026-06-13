package com.example.cinema

data class Movie(
    val id: Long = 0,
    val title: String,
    val director: String,
    val year: Int,
    val genre: String,
    val cost: Double
)
