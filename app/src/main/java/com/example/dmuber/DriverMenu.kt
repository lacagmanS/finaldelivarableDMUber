package com.example.dmuber

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth

class DriverMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_menu)

       val auth = FirebaseAuth.getInstance()

        val buttonPostbooking: ConstraintLayout = findViewById(R.id.postbooking)
        val buttonLiveCarpoolRequest: ConstraintLayout = findViewById(R.id.carpoolrequest)
        val buttonViewOrders: ConstraintLayout = findViewById(R.id.buttonViewOrders)
        val buttonSignOut: ConstraintLayout = findViewById(R.id.buttonSignOut)

        val lightModeBtn: ImageView = findViewById(R.id.lightModeBtn)

        buttonPostbooking.setOnClickListener {
            startActivity(Intent(this, JourneyDateActivity::class.java))
        }
        buttonLiveCarpoolRequest.setOnClickListener {
            startActivity(Intent(this, DriverMapsActivity::class.java))
        }
        buttonViewOrders.setOnClickListener {
            startActivity(Intent(this, BookingHistoryActivity::class.java))
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

    }
}
