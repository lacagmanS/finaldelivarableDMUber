package com.example.dmuber

import android.media.Rating
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewRatingActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val reviews = mutableListOf<String>()  // List to store review strings, corrected naming

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_rating)

        listView = findViewById(R.id.listViewRatings)
        val driverId = intent.getStringExtra("driverId") ?: return

        fetchRatings(driverId)
    }

    private fun fetchRatings(driverId: String) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Reviews").child(driverId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reviews.clear()  // Clear existing reviews before adding new ones
                if (snapshot.exists()) {
                    snapshot.children.forEach { child ->
                        val review = child.getValue(Review::class.java)
                        review?.let {
                            reviews.add("Rating: ${it.rating}\nComment: ${it.comments}")
                        }
                    }
                    val adapter = ArrayAdapter<String>(this@ViewRatingActivity, android.R.layout.simple_list_item_1, reviews)
                    listView.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewRatingActivity, "Failed to load ratings: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}