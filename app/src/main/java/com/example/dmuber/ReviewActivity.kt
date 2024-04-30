package com.example.dmuber


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ReviewActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val driverId = intent.getStringExtra("driverId")

        // Get references to the views
        val ratingBar: RatingBar = findViewById(R.id.ratingBar)
        val editTextComments: EditText = findViewById(R.id.editTextComments)
        val buttonSubmitReview: Button = findViewById(R.id.buttonSubmitReview)

        buttonSubmitReview.setOnClickListener {
            // Get the selected rating and comments
            val rating: Float = ratingBar.rating
            val comments: String = editTextComments.text.toString()

            if (comments.length > 500) {
                showToast("Comments cannot exceed 500 characters.")
            } else {
                // Check if a rating has been selected
                if (rating > 0) {
                    // Add review details to the Realtime Database
                    if (driverId != null) {
                        addReviewToDatabase(rating, comments, driverId)
                        showToast("driver id null blud")
                    }
//                    showToast("Rating Submitted - Thanks for the review!")
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // Display a toast indicating that a rating must be selected
                    showToast("Please select a star rating before submitting your review.")
                }
            }
        }

    }

    private fun addReviewToDatabase(rating: Float, comments: String, driverId: String) {
        val user = auth.currentUser
        val userId = user?.uid

        // Check if the user ID is available
        userId?.let { uid ->
            val database = Firebase.database
            val reviewsRef = database.getReference("Reviews").child(driverId)  // Reviews are now grouped under each driver's ID

            // Create a unique key for each review under this driver
            val reviewId = reviewsRef.push().key

            // Create a Review object assuming the Review constructor is correctly defined
            // Pass the userId of the reviewer, rating, and comments to the Review object
            val review = Review(uid, rating, comments)  // Now Review stores the ID of the user who wrote the review

            // Push the review to the "Reviews" node under the driver ID using the unique key as the identifier
            reviewId?.let { key ->
                reviewsRef.child(key).setValue(review)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Review successfully saved under driver ID: $driverId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Failed to save review under driver ID: $driverId", e)
                    }
            }
        } ?: Log.e("Firebase", "User ID is null, cannot save review")
    }


    private fun showToast(message: String) {
        currentToast?.cancel()
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
}