package com.example.dmuber

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddAdminActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextFullName: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var editTextUserName: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_admin)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextFullName = findViewById(R.id.editTextFullName)
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        editTextUserName = findViewById(R.id.editTextUserName)
        editTextPassword = findViewById(R.id.editTextPassword1)
        editTextConfirmPassword = findViewById(R.id.editTextPassword2)

        findViewById<Button>(R.id.buttonAddAdmin).setOnClickListener {
            // Get admin details from the form
            val email = editTextEmail.text.toString().trim()
            val fullName = editTextFullName.text.toString().trim()
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val userName = editTextUserName.text.toString().trim()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            // Validate inputs
            if (!validateInputs(
                    fullName,
                    userName,
                    email,
                    phoneNumber,
                    password,
                    confirmPassword
                )
            ) {
                return@setOnClickListener
            }

            // Add the admin to Firebase
            addAdminToFirebase(email, fullName, phoneNumber, userName, password)
        }
    }

    private fun validateInputs(
        fullName: String,
        userName: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String
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

        if (phoneNumber.isEmpty() || !android.util.Patterns.PHONE.matcher(phoneNumber)
                .matches() || phoneNumber.length > 15
        ) {
            showToast("Please enter a valid phone number.")
            return false
        }

        if (password.length < 8 || password.length > 20 || password.contains(" ")) {
            showToast("Please enter a password between 8 and 20 characters without spaces.")
            return false
        }

        if (password != confirmPassword) {
            showToast("Password and confirm password do not match.")
            return false
        }


        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this@AddAdminActivity, message, Toast.LENGTH_SHORT).show()
    }


    private fun addAdminToFirebase(
        email: String,
        fullName: String,
        phoneNumber: String,
        userName: String,
        password: String
    ) {
        // Reference to the admins node in the database
        val adminsRef = FirebaseDatabase.getInstance().getReference("Admin")

        // First, check if admin with the same email already exists in Authentication
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful && task.result?.signInMethods?.isEmpty() == true) {
                // Email not in use, create the user
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@AddAdminActivity) { authTask ->
                        if (authTask.isSuccessful) {
                            val user = authTask.result?.user
                            user?.let {
                                val adminId = user.uid  // Use Firebase Auth UID for Database as well
                                val newAdmin = Admin(email, fullName, phoneNumber, userName)

                                // Save the admin details using the same UID from Auth
                                adminsRef.child(adminId).setValue(newAdmin).addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(
                                            this@AddAdminActivity,
                                            "Admin added successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@AddAdminActivity,
                                            "Failed to save admin info to database: ${dbTask.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            // Firebase Authentication failed
                            Toast.makeText(
                                this@AddAdminActivity,
                                "Failed to create user: ${authTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // Admin with this email already exists
                Toast.makeText(
                    this@AddAdminActivity,
                    "Admin with this email already exists",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            // Handle failure cases
            Toast.makeText(
                this@AddAdminActivity,
                "Failed to check existing email: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}