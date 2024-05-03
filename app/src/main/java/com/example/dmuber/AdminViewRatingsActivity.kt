package com.example.dmuber

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AdminViewRatingsActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view_ratings)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser


        displayDriverRatings()
    }

    private fun displayDriverRatings() {

            val reviewsRef = database.getReference("Reviews")
            val driversRef = database.getReference("Drivers")

            currentUser?.let { user ->
                reviewsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (driverSnapshot in dataSnapshot.children) {

                            val driverGroupId = driverSnapshot.key

                            driverGroupId?.let { driverId ->
                                driversRef.child(driverId)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(driverDataSnapshot: DataSnapshot) {
                                            // Get the driver's name from the "fullName" field
                                            val driverName =
                                                driverDataSnapshot.child("fullName").value.toString()

                                            val reviewsLayout =
                                                findViewById<LinearLayout>(R.id.reviewsLayout)

                                            driverSnapshot.children.forEach { reviewSnapshot ->
                                                // Skip the node containing the driver's name
                                                if (reviewSnapshot.key != "driverName") {
                                                    val rating =
                                                        reviewSnapshot.child("rating").value.toString()
                                                    val comments =
                                                        reviewSnapshot.child("comments").value.toString()

                                                    val reviewTextView =
                                                        TextView(this@AdminViewRatingsActivity)
                                                    reviewTextView.text =
                                                        "Driver: $driverName\nRating: $rating\nComments: $comments\n\n"

                                                    reviewsLayout.addView(reviewTextView)
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Handle onCancelled event
                                        }
                                    })
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle onCancelled event
                    }
                })
            }
        }

}