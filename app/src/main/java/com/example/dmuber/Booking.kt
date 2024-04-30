package com.example.dmuber

data class Booking(
    val bookingId: String = "",
    val startTime: String = "",
    val startDestination: String = "",
    val finalDestination: String = "",
    val seatsAvailable: Int? = null,
    val driverName: String = "",
    val price: String = ""
) {
    constructor() : this("", "", "", "", null, "")
}
