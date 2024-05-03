package com.example.dmuber

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DriverArrivedOverviewActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private var driverId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_arrived_overview)
        databaseReference = FirebaseDatabase.getInstance().getReference("DriverArrived")
        driverDetails()


    }

    private fun setupButtonListener(driverId: String) {
        val buttonReview = findViewById<Button>(R.id.buttonReview)
        buttonReview.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)

            intent.putExtra("driverId", driverId)
            startActivity(intent)
        }
    }
    private fun driverDetails(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid

// Find views
        val titleTextView = findViewById<TextView>(R.id.titleTextView)


        titleTextView.text = "Driver Has Arrived"

        userId?.let {
            // Listen for the specific driver arrival information
            databaseReference.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val arrivalTime =
                                snapshot.child("arrivalTime").getValue(String::class.java)
                             driverId = snapshot.child("driverId").getValue(String::class.java)
                            driverId?.let { it1 -> setupButtonListener(it1) }
                            driverId?.let { id ->
                                FirebaseDatabase.getInstance().getReference("Drivers").child(id)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(driverSnapshot: DataSnapshot) {
                                            if (driverSnapshot.exists()) {
                                                val driverDetails =
                                                    driverSnapshot.getValue(DriverDetails::class.java)
                                                driverDetails?.let { details ->
                                                    // Set each TextView with its respective detail
                                                    findViewById<TextView>(R.id.textViewArrivalTime).text = "Arrival Time: $arrivalTime"
                                                    findViewById<TextView>(R.id.textViewDriverName).text =
                                                        "Full Name: ${details.fullName}"
                                                    findViewById<TextView>(R.id.textViewPhoneNumber).text =
                                                        "Phone Number: ${details.phoneNumber}"
                                                    findViewById<TextView>(R.id.textViewLicensePlate).text =
                                                        "License Plate: ${details.licensePlate}"
                                                    findViewById<TextView>(R.id.textViewCarModel).text =
                                                        "Car Model: ${details.carModel}"
                                                    findViewById<TextView>(R.id.textViewSeatsAvailable).text =
                                                        "Seats Available: ${details.seatsAvailable}"
                                                    findViewById<TextView>(R.id.textViewDescription).text =
                                                        "Description: ${details.description}"
                                                    findViewById<TextView>(R.id.textViewStudentEmail).text =
                                                        "Student Email: ${details.email}"


                                                }
                                            } else {
                                                Log.d("Firebase", "No driver details found")
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            Log.e(
                                                "Firebase",
                                                "Failed to read driver details",
                                                databaseError.toException()
                                            )
                                        }
                                    })
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }}










//package com.example.dmuber
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.EditText
//import android.widget.RatingBar
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//
//class DriverArrivedOverviewActivity : AppCompatActivity() {
//    private lateinit var databaseReference: DatabaseReference
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_driver_arrived_overview)
//
//        // Initialize Firebase Database reference
//        databaseReference = FirebaseDatabase.getInstance().getReference("DriverArrived")
//
//        findViewById<Button>(R.id.buttonBack).setOnClickListener {
//            // Intent to start ReviewActivity
//            val intent = Intent(this, ReviewActivity::class.java)
//            startActivity(intent)
//// Get current user's ID
//            val userId = FirebaseAuth.getInstance().currentUser?.uid
//
//// Find views
//            val titleTextView = findViewById<TextView>(R.id.titleTextView)
//
//
//
//            titleTextView.text = "Driver Has Arrived"
//
//// Ensure userId is not null
//
//            userId?.let { uid ->
//                databaseReference.child(uid)
//                    .addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            if (snapshot.exists()) {
//                                val arrivalTime =
//                                    snapshot.child("arrivalTime").getValue(String::class.java)
//                                val driverId =
//                                    snapshot.child("driverId").getValue(String::class.java)
//
//                                driverId?.let { id ->
//                                    FirebaseDatabase.getInstance().getReference("Drivers").child(id)
//                                        .addListenerForSingleValueEvent(object :
//                                            ValueEventListener {
//                                            override fun onDataChange(driverSnapshot: DataSnapshot) {
//                                                if (driverSnapshot.exists()) {
//                                                    val driverDetails =
//                                                        driverSnapshot.getValue(DriverDetails::class.java)
//                                                    driverDetails?.let { details ->
//
//                                                        findViewById<TextView>(R.id.textViewArrivalTime).text =
//                                                            "Arrival Time: $arrivalTime"
//                                                        findViewById<TextView>(R.id.textViewDriverName).text =
//                                                            "Driver Name: ${details.fullName}"
//                                                        findViewById<TextView>(R.id.textViewCarModel).text =
//                                                            "Car Model: ${details.carModel}"
//                                                        findViewById<TextView>(R.id.textViewLicensePlate).text =
//                                                            "License Plate: ${details.licensePlate}"
//                                                        findViewById<TextView>(R.id.textViewPhoneNumber).text =
//                                                            "Phone Number: ${details.phoneNumber}"
//                                                        findViewById<TextView>(R.id.textViewSeatsAvailable).text =
//                                                            "Seats Available: ${details.seatsAvailable}"
//                                                        findViewById<TextView>(R.id.textViewDescription).text =
//                                                            "Description: ${details.description}"
//                                                        findViewById<TextView>(R.id.textViewStudentEmail).text =
//                                                            "Student Email: ${details.studentEmail}"
//                                                    }
//                                                } else {
//                                                    // Handle the case where driver details are not found
//                                                    Log.e(
//                                                        "DriverDetails",
//                                                        "No driver details found"
//                                                    )
//                                                }
//                                            }
//
//                                            override fun onCancelled(databaseError: DatabaseError) {
//                                                Log.e(
//                                                    "Firebase",
//                                                    "Failed to read driver details",
//                                                    databaseError.toException()
//                                                )
//                                            }
//                                        })
//                                }
//                            } else {
//                                // Handle the case where the snapshot does not exist
//                                Log.e("DriverArrived", "No arrival data found")
//                            }
//                        }
//
//                        override fun onCancelled(databaseError: DatabaseError) {
//                            Log.e(
//                                "Firebase",
//                                "Failed to read arrival data",
//                                databaseError.toException()
//                            )
//                        }
//
//                    })
//            }
//        }
//    }
//}
//
//
