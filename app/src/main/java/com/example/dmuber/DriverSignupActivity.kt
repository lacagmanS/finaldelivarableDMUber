package com.example.dmuber

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dmuber.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.sql.Driver

class DriverSignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val editTextFullName = findViewById<EditText>(R.id.editTextFullName)
        val editTextPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        val editTextLicensePlate = findViewById<EditText>(R.id.editTextLicensePlate)
        val editTextStudentEmail = findViewById<EditText>(R.id.editTextStudentEmail)
        val editTextDriverStatus = findViewById<EditText>(R.id.editTextDriverStatus)
        val editTextDescription = findViewById<EditText>(R.id.editTextDescription)
        val editTextSeatsAvailable = findViewById<EditText>(R.id.editTextSeatsAvailable)
        val editTextCarModel = findViewById<EditText>(R.id.editTextCarModel)
        val textViewMessage = findViewById<TextView>(R.id.textViewMessage)
        val textViewLogin = findViewById<TextView>(R.id.textViewSignIn)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)


        buttonRegister.setOnClickListener {
            val fullName = editTextFullName.text.toString().trim()
            val email = editTextStudentEmail.text.toString().trim()
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val licensePlate = editTextLicensePlate.text.toString().trim()

            val studentEmail = editTextStudentEmail.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            val seatsAvailable = editTextSeatsAvailable.text.toString().trim()
            val carModel = editTextCarModel.text.toString().trim()

            if (validateInputs(
                    fullName,
                    phoneNumber,
                    password,
                    licensePlate,
                    studentEmail,
                    description,
                    seatsAvailable,
                    carModel)
            ) {
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
        phoneNumber: String,
        password: String,
        licensePlate: String,
        studentEmail: String,
        description: String,
        seatsAvailable: String,
        carModel: String
    ): Boolean {
        // Validate each input field and show error messages if needed
        if (fullName.isEmpty() || fullName.length < 1 || fullName.length > 50) {
            showToast("Please enter a valid full name (1-50 characters).")
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

        if (licensePlate.isEmpty() || licensePlate.length < 2 || licensePlate.length > 20) {
            showToast("Please enter a license plate between 1-20 characters.")
            return false
        }


        if (studentEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(studentEmail).matches()) {
            showToast("Please enter a valid student email address.")
            return false
        }


        if (description.isEmpty() || description.length < 2 || description.length > 500) {
            showToast("Please enter a description between 1-500 characters.")
            return false
        }


        if (seatsAvailable.isEmpty() || seatsAvailable.toIntOrNull() == null || seatsAvailable.toInt() < 1) {
            showToast("Please enter a valid number of seats available.")
            return false
        }

        if (carModel.isEmpty() || carModel.length < 2 || carModel.length > 20) {
            showToast("Please enter a car model description between 1-20 characters.")
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
            val usersRef = database.getReference("Drivers")

            val editTextFullName = findViewById<EditText>(R.id.editTextFullName)
            val editTextPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
            val editTextLicensePlate = findViewById<EditText>(R.id.editTextLicensePlate)
            val editTextStudentEmail = findViewById<EditText>(R.id.editTextStudentEmail)
           val editTextDescription = findViewById<EditText>(R.id.editTextDescription)

            val editTextSeatsAvailable = findViewById<EditText>(R.id.editTextSeatsAvailable)
            val editTextCarModel = findViewById<EditText>(R.id.editTextCarModel)

            val fullName = editTextFullName.text.toString().trim()
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val licensePlate = editTextLicensePlate.text.toString().trim()

            val studentEmail = editTextStudentEmail.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            val seatsAvailable = editTextSeatsAvailable.text.toString().trim()
            val carModel = editTextCarModel.text.toString().trim()

            val DriverDetails = DriverDetails(
                fullName,
                phoneNumber,
                licensePlate,
                studentEmail,
                description,
                seatsAvailable,
                carModel
            )

            // Push user details to the "Drivers" node using the user's UID as the key
            usersRef.child(userId).setValue(DriverDetails)
        }
    }

    private fun showToast(message: String) {
        currentToast?.cancel()
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

}
