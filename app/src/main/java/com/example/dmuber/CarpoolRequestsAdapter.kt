package com.example.dmuber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CarpoolRequestsAdapter(private val requests: List<carpoolrequestsheet>) :
    RecyclerView.Adapter<CarpoolRequestsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val customerName: TextView = itemView.findViewById(R.id.customer_name)
        val distance: TextView = itemView.findViewById(R.id.distance)
        val acceptButton: Button = itemView.findViewById(R.id.accept_button)
        val declineButton: Button = itemView.findViewById(R.id.decline_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carpool_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]

        holder.customerName.text = request.customerName
        holder.distance.text = request.distance.toString()

        holder.acceptButton.setOnClickListener {
            val carpoolRequestsRef = FirebaseDatabase.getInstance().getReference("CarpoolRequests")


            carpoolRequestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Iterate through each child node
                    for (childSnapshot in dataSnapshot.children) {
                        // Get the UID and customer destination from the child node
                        val uid = childSnapshot.child("uid").getValue(String::class.java)
                        val destination = childSnapshot.child("destination")
                        val latitude = destination.child("latitude").getValue(Double::class.java)
                        val longitude = destination.child("longitude").getValue(Double::class.java)

                        // Check if the UID and destination values are not null
                        if (uid != null && latitude != null && longitude != null) {
                            // Construct the customer location string
                            val customerLocation = "$latitude,$longitude"

                            // Construct the accepted request data
                            val acceptedRequestData = HashMap<String, Any>()
                            acceptedRequestData["customerid"] = uid
                            acceptedRequestData["CustomerLocation"] = customerLocation
                            acceptedRequestData["CustomerDestination"] = "$latitude,$longitude" // You may format this as needed
                            acceptedRequestData["DriverID"] = FirebaseAuth.getInstance().currentUser!!.uid

                            // Push the data to the "acceptedRequests" node
                            carpoolRequestsRef.push().setValue(acceptedRequestData)

                            // Exit the loop if the desired UID is found
                            break
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })

        }


        holder.declineButton.setOnClickListener {
            Snackbar.make(
                holder.itemView,
                "You declined the request from ${request.customerName}",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}
