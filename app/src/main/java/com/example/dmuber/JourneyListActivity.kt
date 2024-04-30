package com.example.dmuber

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class JourneyListActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var bookingList: MutableList<Booking>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey_list)

        bookingRecyclerView = findViewById(R.id.bookingRecyclerView)
        bookingRecyclerView.layoutManager = LinearLayoutManager(this)
        bookingList = mutableListOf()
        bookingAdapter = BookingAdapter(bookingList) { booking -> onBookingClicked(booking) }
        bookingRecyclerView.adapter = bookingAdapter

        database = FirebaseDatabase.getInstance()

        val selectedDay = intent.getIntExtra("selected_day", -1)
        val selectedMonth = intent.getIntExtra("selected_month", -1)
        val selectedYear = intent.getIntExtra("selected_year", -1)

        if (selectedDay != -1 && selectedMonth != -1 && selectedYear != -1) {
            loadBookings(selectedDay, selectedMonth, selectedYear)
        } else {
            Toast.makeText(this, "Error: Date not selected", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if date is not selected
        }
    }

    private fun loadBookings(selectedDay: Int, selectedMonth: Int, selectedYear: Int) {
        val dateString = "$selectedDay/${selectedMonth + 1}/$selectedYear"

        val bookingsRef = database.getReference("PostedBookings")
        bookingsRef.orderByChild("date").equalTo(dateString).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                bookingList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val booking = postSnapshot.getValue(Booking::class.java)
                    booking?.let {
                        bookingList.add(it)
                    }
                }
                bookingAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                val errorMessage = "Database Error: ${databaseError.message}"
                Toast.makeText(this@JourneyListActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onBookingClicked(booking: Booking) {
        val bookingID = booking.bookingId
        val intent = Intent(this, BookingDetailsActivity::class.java)
        intent.putExtra("bookingId", bookingID)
        startActivity(intent)
    }
}
