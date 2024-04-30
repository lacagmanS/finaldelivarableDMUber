package com.example.dmuber
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dmuber.AdminPanelActivity
import com.example.dmuber.MainActivity
import com.example.dmuber.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val buttonSignIn = findViewById<Button>(R.id.buttonSignIn)
        val editTextEmailSignIn = findViewById<EditText>(R.id.editTextEmailSignIn)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val textViewSignUp = findViewById<TextView>(R.id.textViewSignUp)

        buttonSignIn.setOnClickListener {
            val email = editTextEmailSignIn.text.toString()
            val password = editTextPassword.text.toString()

            if (validateInputs(email, password)) {
                signIn(email, password)
            }
        }

        textViewSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address.")
            return false
        }

        if (password.length < 8 || password.length > 20 || password.contains(" ")) {
            showToast("Please enter a valid password")
            return false
        }
        return true
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userId = user.uid

                        // Check the user type (Admin or Customer) based on the user ID
                        checkUserType(userId)
                    } else {
                        // Handle the case where the user is unexpectedly null
                        showToast("Authentication failed. User is null.")
                    }
                } else {
                    showToast("Authentication failed. Check your credentials.")
                }
            }
    }

    private fun checkUserType(userId: String) {
        val adminRef = FirebaseDatabase.getInstance().getReference("Admin").child(userId)
        val customerRef = FirebaseDatabase.getInstance().getReference("Customers").child(userId)
        val driverRef = FirebaseDatabase.getInstance().getReference("Drivers").child(userId)

        adminRef.get().addOnCompleteListener { adminTask ->
            if (adminTask.isSuccessful) {
                if (adminTask.result != null && adminTask.result.exists()) {
                    val intent = Intent(this, AdminPanelActivity::class.java)
                    startActivity(intent)
                    finish()
                    showToast("Admin sign in successful")
                } else {
                    // Check if the user is a customer
                    customerRef.get().addOnCompleteListener { customerTask ->
                        if (customerTask.isSuccessful) {
                            if (customerTask.result != null && customerTask.result.exists()) {
                                // User is a customer
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                showToast("Passenger sign in successful")
                            } else {
                                // Check if the user is a driver
                                driverRef.get().addOnCompleteListener { driverTask ->
                                    if (driverTask.isSuccessful) {
                                        if (driverTask.result != null && driverTask.result.exists()) {
                                            // User is a driver
                                            val intent = Intent(this, DriverMenu::class.java)
                                            startActivity(intent)
                                            finish()
                                            showToast("Driver sign in successful")
                                        } else {
                                            showToast("User not found in database.")
                                        }
                                    } else {
                                        showToast("Error checking driver user type.")
                                    }
                                }
                            }
                        } else {
                            showToast("Error checking customer user type.")
                        }
                    }
                }
            } else {
                showToast("Error checking admin user type.")
            }

    }

}



    private fun showToast(message: String) {
        currentToast?.cancel()
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish()
    }
}

