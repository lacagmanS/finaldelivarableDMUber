package com.example.dmuber

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth

class AdminPanelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel) // Make sure to use the correct layout file name

        val auth = FirebaseAuth.getInstance()

        val buttonViewRatings: ConstraintLayout = findViewById(R.id.buttonViewOrders)
        val buttonAddAdmin: ConstraintLayout = findViewById(R.id.buttonAddAdmin)
        val buttonSignOut: ConstraintLayout = findViewById(R.id.buttonSignOut)
        val removeadmin : ConstraintLayout = findViewById((R.id.removeadmin))

        val lightModeBtn: ImageView = findViewById(R.id.lightModeBtn)

        buttonViewRatings.setOnClickListener {
            startActivity(Intent(this, AdminViewRatingsActivity::class.java)) // Replace with the correct Activity class
        }
        buttonAddAdmin.setOnClickListener {
            startActivity(Intent(this, AddAdminActivity::class.java)) // Assume there's an Activity to add admins
        }
        removeadmin.setOnClickListener {
            startActivity(Intent(this, AdminManageActivity::class.java)) // Assume there's an Activity to add admins
        }

        buttonSignOut.setOnClickListener {
            auth.signOut()

            // Close the current activity
            finish()

            // Start the login or start activity
            val intent = Intent(this, StartActivity::class.java) // Replace with your login or start Activity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        lightModeBtn.setOnClickListener {
            toggleTheme()
        }
    }

    private fun toggleTheme() {
        // This method toggles the day/night theme for the app
        val mode = AppCompatDelegate.getDefaultNightMode()
        if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    override fun onBackPressed() {
        // Optionally handle the back button; for now we do nothing
        super.onBackPressed()
    }
}
