package com.example.dmuber

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.dmuber.R

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val buttonGoToLogin = findViewById<Button>(R.id.buttonLogin)
        buttonGoToLogin.setOnClickListener {
            // Handle the button click to go to the Login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val buttonGoToSignup = findViewById<Button>(R.id.buttonSignUp)
        buttonGoToSignup.setOnClickListener {
            // Handle the button click to go to the Signup activity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

}
