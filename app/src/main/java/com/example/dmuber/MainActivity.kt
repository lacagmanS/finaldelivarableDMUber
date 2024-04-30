package com.example.dmuber


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonGoToMenu: Button = findViewById(R.id.buttonGoToMenu)
        val buttonLeaveReview: Button = findViewById(R.id.buttonLeaveReview)
        val buttonViewOrders: Button = findViewById(R.id.buttonViewOrders)
        val buttonSignOut: Button = findViewById(R.id.buttonSignOut)

        buttonGoToMenu.setOnClickListener {
            startActivity(Intent(this, BookRideActivity::class.java))
        }
        buttonViewOrders.setOnClickListener {
            startActivity(Intent(this, MapsViews::class.java))
        }
        buttonSignOut.setOnClickListener {
            startActivity(Intent(this, AddAdminActivity::class.java))
        }

    }


    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
