package com.example.dmuber
import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dmuber.BookingDetailsActivity
import com.example.dmuber.R
import java.util.*

class JourneyDateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_ride)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        // Set the minimum date to today
        calendarView.minDate = System.currentTimeMillis()

        // Set the listener for date change
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Display a toast with the selected date
            Toast.makeText(
                this@JourneyDateActivity,
                "Selected date: $dayOfMonth/${month + 1}/$year",
                Toast.LENGTH_SHORT
            ).show()

            // Open the BookingDetailsActivity with the selected date
            openBookingDetailsActivity(dayOfMonth, month + 1, year)
        }
    }

    private fun openBookingDetailsActivity(day: Int, month: Int, year: Int) {
        val intent = Intent(this, JourneyDetailsActivity::class.java)
        intent.putExtra("selected_day", day)
        intent.putExtra("selected_month", month)
        intent.putExtra("selected_year", year)
        startActivity(intent)
    }
}
