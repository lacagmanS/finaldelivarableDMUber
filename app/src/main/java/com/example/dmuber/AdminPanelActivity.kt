package com.example.dmuber

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AdminPanelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)
    }

//    fun onViewOrdersClicked(view: View) {
//        val intent = Intent(this, ViewOrdersActivity::class.java)
//        startActivity(intent)
//    }

    fun onViewRatingsClicked(view: View) {
        val intent = Intent(this, AdminViewRatingsActivity::class.java)
        startActivity(intent)
    }

//    fun onManageItemsClicked(view: View) {
//        val intent = Intent(this, ManageItemsActivity::class.java)
//        startActivity(intent)
//    }

    fun onSignOutClicked(view: View) {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, UserTypeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    fun onAddAdminClicked(view: View) {
        val intent = Intent(this, AddAdminActivity::class.java)
        startActivity(intent)

    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Do nothing to disable the back button
    }





}