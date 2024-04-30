package com.example.dmuber
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dmuber.MainActivity
import com.example.dmuber.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val editTextFullName = findViewById<EditText>(R.id.editTextFullName)
        val editTextUsername = findViewById<EditText>(R.id.editTextEmailSignIn)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        val editTextPassword = findViewById<EditText>(R.id.editTextNewPassword)
        val textViewMessage = findViewById<TextView>(R.id.textViewMessage)
        val textViewLogin = findViewById<TextView>(R.id.textViewSignIn)

        buttonRegister.setOnClickListener {
            val fullName = editTextFullName.text.toString().trim()
            val userName = editTextUsername.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (validateInputs(fullName, userName, email, phoneNumber, password)) {
                // Inputs are valid, register the user in Firebase Authentication
                registerUser(email, password)
            }
        }

        textViewLogin.setOnClickListener {
            // Navigate to SignupActivity when textViewSignUp is clicked
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(
        fullName: String,
        userName: String,
        email: String,
        phoneNumber: String,
        password: String
    ): Boolean {
        // Validate each input field and show error messages if needed
        if (fullName.isEmpty() || fullName.length < 1 || fullName.length > 50) {
            showToast("Please enter a valid full name (1-50 characters).")
            return false
        }

        if (userName.isEmpty() || userName.length < 1 || userName.length > 50) {
            showToast("Please enter a valid username (1-50 characters).")
            return false
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address.")
            return false
        }

        if (phoneNumber.isEmpty() || !android.util.Patterns.PHONE.matcher(phoneNumber).matches() || phoneNumber.length > 15) {
            showToast("Please enter a valid phone number.")
            return false
        }

        if (password.length < 8 || password.length > 20 || password.contains(" ")) {
            showToast("Please enter a password between 8 and 20 characters without spaces.")
            return false
        }

        // All inputs are valid
        return true
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    showToast("Registration successful.")
                    // Add user details to the Realtime Database
                    addUserDetailsToDatabase(user?.uid)

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If registration fails, display a message to the user.
                    showToast("Registration failed. ${task.exception?.message}")
                }
            }
    }

    private fun addUserDetailsToDatabase(userId: String?) {
        userId?.let {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("Customers")
            val editTextFullName = findViewById<EditText>(R.id.editTextFullName)
            val editTextUsername = findViewById<EditText>(R.id.editTextEmailSignIn)
            val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
            val editTextPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)

            val fullName = editTextFullName.text.toString().trim()
            val userName = editTextUsername.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val customerId = userId.toString()
            val userDetails = UserDetails(fullName, userName, email, phoneNumber, customerId)

            // Push user details to the "users" node using the user's UID as the key
            usersRef.child(userId).setValue(userDetails)
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
