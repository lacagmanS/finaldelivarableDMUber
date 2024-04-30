package com.example.dmuber

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JourneyDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey_details)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize UI elements
        val editTextStartTime = findViewById<EditText>(R.id.editTextStartTime)
        val editTextStartDestination = findViewById<EditText>(R.id.editTextStartDestination)
        val editTextFinalDestination = findViewById<EditText>(R.id.editTextFinalDestination)
        val editTextDescription = findViewById<EditText>(R.id.editTextDescription)
        val editTextSeatsAvailable = findViewById<EditText>(R.id.editTextSeatsAvailable)
        val editTextPrice = findViewById<EditText>(R.id.editTextPrice)

        val buttonPostJourney = findViewById<Button>(R.id.buttonPostJourney)

        // Set click listener for Post Journey button
        buttonPostJourney.setOnClickListener {
            // Get input values
            val startTime = editTextStartTime.text.toString().trim()
            val startDestination = editTextStartDestination.text.toString().trim()
            val finalDestination = editTextFinalDestination.text.toString().trim()
            val description = editTextDescription.text.toString().trim()
            val seatsAvailable = editTextSeatsAvailable.text.toString().toInt()
            val price = editTextPrice.text.toString().trim()
            // Validate inputs
            if (validateInputs(
                    startTime,
                    startDestination,
                    finalDestination,
                    price,
                    description,
                    seatsAvailable
                )
            ) {

                postJourney(
                    startTime,
                    startDestination,
                    finalDestination,
                    price,
                    description,
                    seatsAvailable
                )
            }
        }
    }

    private fun validateInputs(
        startTime: String,
        startDestination: String,
        finalDestination: String,
        price: String,
        description: String,
        seatsAvailable: Int
    ): Boolean {
        // Minimum and maximum lengths for each input field
        val minStartTimeLength = 1
        val maxStartTimeLength = 50

        val minDestinationLength = 1
        val maxDestinationLength = 100

        val minDescriptionLength = 1
        val maxDescriptionLength = 500

        val minSeatsAvailableLength = 1
        val maxSeatsAvailableLength = 2


        val minPriceLength = 1 // Minimum length for price
        val maxPriceLength = 10

        // Check if any of the input fields are empty
        if (startTime.isEmpty() || startTime.length !in minStartTimeLength..maxStartTimeLength) {
            Toast.makeText(
                this,
                "Please enter a valid start time (1-50 characters)",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (startDestination.isEmpty() || startDestination.length !in minDestinationLength..maxDestinationLength) {
            Toast.makeText(
                this,
                "Please enter a valid start destination (1-100 characters)",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (finalDestination.isEmpty() || finalDestination.length !in minDestinationLength..maxDestinationLength) {
            Toast.makeText(
                this,
                "Please enter a valid final destination (1-100 characters)",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (description.isEmpty() || description.length !in minDescriptionLength..maxDescriptionLength) {
            Toast.makeText(
                this,
                "Please enter a valid description (1-500 characters)",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }


        if (price.isEmpty() || price.length !in minPriceLength..maxPriceLength) { // Check price length
            Toast.makeText(this, "Please enter a valid price (1-10 characters)", Toast.LENGTH_SHORT)
                .show()
            return false
        }


        // Validate the seats available field to ensure it is a valid integer

        if (seatsAvailable == null || seatsAvailable < 1) {
            Toast.makeText(
                this,
                "Please enter a valid number of seats available",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (startTime.isEmpty()) {
            Toast.makeText(this, "Please enter a start time", Toast.LENGTH_SHORT).show()
            return false
        }

        if (startDestination.isEmpty()) {
            Toast.makeText(this, "Please enter a start destination", Toast.LENGTH_SHORT).show()
            return false
        }

        if (finalDestination.isEmpty()) {
            Toast.makeText(this, "Please enter a final destination", Toast.LENGTH_SHORT).show()
            return false
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
            return false
        }

        if (seatsAvailable <= 0) {
            Toast.makeText(this, "Please enter a valid number of seats available", Toast.LENGTH_SHORT).show()
            return false
        }

        if (price.isEmpty()) {
            Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun postJourney(
        startTime: String,
        startDestination: String,
        finalDestination: String,
        price: String,
        description: String,
        seatsAvailable: Int
    ) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val driverId = user.uid

            // Retrieve the selected date from the Intent extras
            val selectedDay = intent.getIntExtra("selected_day", -1)
            val selectedMonth = intent.getIntExtra("selected_month", -1)
            val selectedYear = intent.getIntExtra("selected_year", -1)

            if (selectedDay != -1 && selectedMonth != -1 && selectedYear != -1) {
                // Construct the full date string
                val dateString = "$selectedDay/${selectedMonth + 1}/$selectedYear"

                // Query the user details from the database
                val usersRef = database.getReference("Drivers")
                val userQuery = usersRef.child(driverId)
                userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val driver = dataSnapshot.getValue(DriverDetails::class.java)
                            if (driver != null) {
                                // Get driver details from the database
                                val driverName = driver.fullName ?: ""
                                val driverPhone = driver.phoneNumber ?: ""
                                val driverLicense = driver.licensePlate ?: ""
                                val carModel = driver.carModel ?: ""



                                // Include the date and driver details in the journey object
                                val bookingRef = database.getReference("PostedBookings").push()
                                val bookingId = bookingRef.key
                                val bookingID= bookingId.toString()
                                val booking = PostedBooking(
                                    bookingID,
                                    startTime,
                                    startDestination,
                                    finalDestination,
                                    description,
                                    price,
                                    seatsAvailable,
                                    driverId,
                                    driverName,
                                    driverPhone,
                                    driverLicense,
                                    carModel,
                                    dateString // Include the date in the booking
                                )
                                // Push the journey to the database
                                bookingRef.setValue(booking)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@JourneyDetailsActivity,
                                            "Journey posted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this@JourneyDetailsActivity,
                                            "Failed to post journey",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle cancelled event
                        // This method will be called if the listener is canceled for any reason
                        // Handle errors or cleanup tasks here
                    }
                })
            } else {
                Toast.makeText(this, "Error: Date not selected", Toast.LENGTH_SHORT).show()
            }
        }
    }



}