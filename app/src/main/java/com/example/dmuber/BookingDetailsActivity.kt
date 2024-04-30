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

class BookingDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var bookingId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        // Get the booking ID from the intent extras
        bookingId = intent.getStringExtra("bookingId") ?: ""

        // Retrieve booking details from Firebase Database
        fetchBookingDetails()

        // Set up button click listener
        findViewById<Button>(R.id.buttonBookNow).setOnClickListener {
            // Implement logic to book the journey
            bookNow()
        }

    }

    private fun fetchBookingDetails() {
        // Reference to the specific booking using the booking ID
        val bookingRef = database.getReference("PostedBookings").child(bookingId)

        // Listen for changes in the booking data
        bookingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the booking exists
                if (dataSnapshot.exists()) {
                    // Retrieve booking details from the dataSnapshot
                    val booking = dataSnapshot.getValue(PostedBooking::class.java)


                    displayBookingDetails(booking)
                    setupRatingsButton(booking)
                } else {
                    // Booking does not exist
                    Toast.makeText(this@BookingDetailsActivity, "Booking not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                val errorMessage = "Database Error: ${databaseError.message}"
                Toast.makeText(this@BookingDetailsActivity, errorMessage, Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun displayBookingDetails(booking: PostedBooking?) {

        findViewById<TextView>(R.id.textViewStartTime).text = "Start Time: ${booking?.startTime}"
        findViewById<TextView>(R.id.textViewStartDestination).text = "Start Destination: ${booking?.startDestination}"
        findViewById<TextView>(R.id.textViewFinalDestination).text = "Final Destination: ${booking?.finalDestination}"
        findViewById<TextView>(R.id.textViewPrice).text = "Price: ${booking?.price}"
        findViewById<TextView>(R.id.textViewDescription).text = "Description: ${booking?.description}"
        findViewById<TextView>(R.id.textViewSeatsAvailable).text = "Seats Available: ${booking?.seatsAvailable}"
        findViewById<TextView>(R.id.textViewDriverName).text = "Driver Name: ${booking?.driverName}"
        findViewById<TextView>(R.id.textViewDriverPhone).text = "Driver Phone: ${booking?.driverPhone}"
        findViewById<TextView>(R.id.textViewDriverLicense).text = "Driver License: ${booking?.driverLicense}"
        findViewById<TextView>(R.id.textViewCarModel).text = "Car Model: ${booking?.carModel}"
    }


    private fun bookNow() {
        val currentUser = auth.currentUser
        val customerId = currentUser?.uid ?: ""

        // Get a reference to the database nodes
        val postedBookingsRef = database.getReference("PostedBookings").child(bookingId)
        val acceptedBookingsRef = database.getReference("AcceptedBookings").push()

        // Retrieve the current seats available count
        postedBookingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val postedBooking = dataSnapshot.getValue(PostedBooking::class.java)
                postedBooking?.let {
                    val currentSeatsAvailable = it.seatsAvailable


                    if (currentSeatsAvailable!! > 0) {
                        // Create a new accepted booking entry
                        val acceptedBooking = mapOf(
                            "driverId" to it.driverId,
                            "bookingId" to bookingId,
                            "customerId" to customerId,
                            "seatsAvailable" to currentSeatsAvailable - 1
                        )

                        // Update AcceptedBookings node
                        acceptedBookingsRef.setValue(acceptedBooking)
                            .addOnSuccessListener {
                                // Update PostedBookings node with decreased seats available count
                                postedBookingsRef.child("seatsAvailable").setValue(currentSeatsAvailable - 1)
                                Toast.makeText(this@BookingDetailsActivity, "Booking successful", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@BookingDetailsActivity, "Failed to book journey", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this@BookingDetailsActivity, "No available seats", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                val errorMessage = "Database Error: ${databaseError.message}"
                Toast.makeText(this@BookingDetailsActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRatingsButton(booking: PostedBooking?) {
        val buttonViewMap = findViewById<Button>(R.id.buttonViewRating)
        buttonViewMap.setOnClickListener {
            // Start MapActivity
            val intent = Intent(this, ViewRatingActivity::class.java)
            val driverId = booking?.driverId
            intent.putExtra("driverId", driverId)
            Log.d("$driverId dont bemull","$driverId")
            startActivity(intent)
        }
    }
}