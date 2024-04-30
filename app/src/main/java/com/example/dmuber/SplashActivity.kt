package com.example.dmuber
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.dmuber.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay and then start the main activity
        Handler(Looper.myLooper()!!).postDelayed({
            val intent = Intent(this, UserTypeActivity::class.java)
            startActivity(intent)
            finish()
        }, 500)
    }
}
