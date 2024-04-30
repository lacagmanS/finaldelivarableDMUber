package com.example.dmuber

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.dmuber.R

class UserTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_type)

        val btnDriver = findViewById<Button>(R.id.btnDriver)
        val btnPassenger = findViewById<Button>(R.id.btnPassenger)

        btnDriver.setOnClickListener {
            startActivity(Intent(this, StartDriverActivty::class.java))
        }

        btnPassenger.setOnClickListener {
            startActivity(Intent(this, StartActivity::class.java))
        }
    }
}
