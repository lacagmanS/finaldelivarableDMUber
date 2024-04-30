package com.example.dmuber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(
    private val bookingList: List<Booking>,
    private val onBookingClicked: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewStartTime: TextView = itemView.findViewById(R.id.textViewStartTime)
        val textViewStartDestination: TextView = itemView.findViewById(R.id.textViewStartDestination)
        val textViewFinalDestination: TextView = itemView.findViewById(R.id.textViewFinalDestination)
        val textViewSeatsAvailable: TextView = itemView.findViewById(R.id.textViewSeatsAvailable)
        val textViewDriverName: TextView = itemView.findViewById(R.id.textViewDriverName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val textViewId: TextView = itemView.findViewById(R.id.Id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_row, parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val currentBooking = bookingList[position]
        holder.textViewId.text = "Booking ID: ${currentBooking.bookingId}"

        holder.textViewStartTime.text = "Start Time: ${currentBooking.startTime}"
        holder.textViewStartDestination.text = "Start Destination: ${currentBooking.startDestination}"
        holder.textViewFinalDestination.text = "Final Destination: ${currentBooking.finalDestination}"
        holder.textViewSeatsAvailable.text = "Seats Available: ${currentBooking.seatsAvailable}"
        holder.textViewDriverName.text = "Driver Name: ${currentBooking.driverName}"
        holder.textViewPrice.text = "Price: ${currentBooking.price}"

        holder.itemView.setOnClickListener {
            // Call the lambda function to handle the click event
            onBookingClicked(currentBooking)
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }
}
