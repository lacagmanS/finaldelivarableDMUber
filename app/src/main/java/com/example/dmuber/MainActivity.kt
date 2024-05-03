package com.example.dmuber

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()

        val buttonFindBooking: ConstraintLayout = findViewById(R.id.findBooking)
//        val buttonLeaveReview: ConstraintLayout = findViewById(R.id.buttonLeaveReview)
        val buttonLiveCarpoolRequest: ConstraintLayout = findViewById(R.id.carpoolRequest)
        val buttonSignOut: ConstraintLayout = findViewById(R.id.buttonSignOut)

        val lightModeBtn: ImageView = findViewById(R.id.lightModeBtn)

        buttonFindBooking.setOnClickListener {
            startActivity(Intent(this, BookRideActivity::class.java))
        }
//        buttonLeaveReview.setOnClickListener {
//            startActivity(Intent(this, ReviewActivity::class.java)) // Assuming the correct Activity is ReviewActivity
//        }
        buttonLiveCarpoolRequest.setOnClickListener {
            startActivity(Intent(this, MapsViews::class.java))
        }
        buttonSignOut.setOnClickListener {
            auth.signOut()

            // Close the current activity
            finish()

            // Start the login or start activity
            val intent = Intent(this, StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        lightModeBtn.setOnClickListener {
            toggleTheme()
        }
    }

    private fun toggleTheme() {
        // Check the current theme mode and switch to the other
        val mode = AppCompatDelegate.getDefaultNightMode()
        if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }


    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
