package com.example.dmuber

import com.google.android.gms.maps.model.LatLng

data class CarpoolRequest(
    val uid: String?,
    val destination: LatLng?,
    val time: String?
)
