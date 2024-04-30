package com.example.dmuber

data class PostedBooking(
    val bookingId: String = "",
    val startTime: String = "",
    val startDestination: String = "",
    val finalDestination: String = "",
    val price: String = "",
    val description: String = "",
    val seatsAvailable: Int? = null,
    val driverId: String = "",
    val driverName: String = "",
    val driverPhone: String = "",
    val driverLicense: String = "",
    val carModel: String = "",
    val date: String = ""
)