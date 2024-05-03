package com.example.dmuber
import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dmuber.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.firebase.geofire.core.GeoHash
import com.firebase.geofire.util.GeoUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MapsViews : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener  {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView

    private var carpoolRequestKey: String? = null

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    private lateinit var onlineRef: DatabaseReference
    private lateinit var currentUserRef: DatabaseReference
    private lateinit var CustomerLocationReference: DatabaseReference
    private lateinit var geoFire: GeoFire
    private lateinit var AlertDialogs: AlertDialog
    val uid = FirebaseAuth.getInstance().currentUser?.uid


    private lateinit var progressDialog: ProgressDialog

    private val onlineValueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            Snackbar.make(findViewById(android.R.id.content), error.message, Snackbar.LENGTH_LONG)
                .show()
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                currentUserRef.onDisconnect().removeValue()
            }
        }
    }
    private lateinit var acceptedRequestsRef: DatabaseReference
    private val acceptedRequestsListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)) {
                showDriverOnWaySnackbar()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle onCancelled event
        }
    }


    private var userMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        progressDialog = ProgressDialog(this)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        init()

    }

    private fun init() {
        onlineRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        CustomerLocationReference =
            FirebaseDatabase.getInstance().getReference("CustomerLocationReference")
        currentUserRef = FirebaseDatabase.getInstance().getReference("CustomerLocationReference")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        geoFire = GeoFire(CustomerLocationReference)

        registerOnlineSystem()

        locationRequest = LocationRequest()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fastestInterval = 3000
            interval = 5000
            smallestDisplacement = 10f
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                location?.let { updateLocation(it) }
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                12
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.setOnMapLongClickListener(this)
        mMap.setOnMarkerClickListener(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun updateLocation(location: Location) {
        val newPos = LatLng(location.latitude, location.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 18f))
        geoFire.setLocation(
            FirebaseAuth.getInstance().currentUser!!.uid,
            GeoLocation(location.latitude, location.longitude)
        ) { key: String?, error: DatabaseError? ->
            if (error != null) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    error.message,
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "You're online!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun registerOnlineSystem() {
        onlineRef.addValueEventListener(onlineValueEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        geoFire.removeLocation(FirebaseAuth.getInstance().currentUser!!.uid)
        onlineRef.removeEventListener(onlineValueEventListener)
        carpoolRequestKey?.let { key ->
            val requestsRef = FirebaseDatabase.getInstance().getReference("CarpoolRequests").child(key)
            requestsRef.removeValue()
        }
        if (this::acceptedRequestsRef.isInitialized) {
            acceptedRequestsRef.removeEventListener(acceptedRequestsListener)
        }

    }
    private fun setupAcceptedRequestsListener() {
        acceptedRequestsRef = FirebaseDatabase.getInstance().getReference("AcceptedCarpoolRequest")
        acceptedRequestsRef.addValueEventListener(acceptedRequestsListener)
    }

    override fun onMapLongClick(latLng: LatLng) {
        addMarkerToMap(latLng)
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker == userMarker) {
            showLiveCarpoolRequestButton(marker)
        }
        return false
    }


    private fun showLiveCarpoolRequestButton(marker: Marker) {
        val button = Button(this)
        button.text = "Live Carpool Request"
        button.setOnClickListener {

            sendCarpoolRequest(marker.position)
            progressDialog.show()
        }
       AlertDialogs =
       AlertDialog.Builder(this)
            .setView(button)
           .show()

        progressDialog.setOnDismissListener {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val requestsRef = FirebaseDatabase.getInstance().getReference("CarpoolRequests").child(uid)
            requestsRef.removeValue()

        }
    }

    private fun sendCarpoolRequest(destination: LatLng) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentTime = getCurrentTime()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val uid = currentUser!!.uid
                    val requestRef =
                        FirebaseDatabase.getInstance().getReference("CarpoolRequests").child(uid)
                    val request = mapOf(
                        "uid" to uid,
                        "destination" to destination,
                        "time" to currentTime,
                        "currentLocation" to LatLng(location.latitude, location.longitude)
                    )
                    val acceptedBookingsRef =
                        FirebaseDatabase.getInstance().getReference("AcceptedCarpoolRequest")
                            .child(uid)
                    acceptedBookingsRef.removeValue().addOnCompleteListener { removalTask ->
                        if (removalTask.isSuccessful) {

                            setupAcceptedRequestsListener()
                            requestRef.setValue(request)
                        } else {
                            Snackbar.make(
                                findViewById(android.R.id.content),
                                "Sorry there was a database error please try again",
                                Snackbar.LENGTH_LONG
                            ).show()


                            Log.e(
                                "CarpoolRequest",
                                "Failed to remove existing booking: ${removalTask.exception}"
                            )

                        }



                    }
                .addOnFailureListener { exception ->
                    // Handle failure to retrieve the location
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Failed to get current location: ${exception.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }}}
        } else {
            // Location permission not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        return dateFormat.format(currentTime)
    }

    private fun addMarkerToMap(latLng: LatLng) {
        if (::userMarker != null) {
            userMarker?.remove()
        }
        userMarker = mMap.addMarker(MarkerOptions().position(latLng).title("Destination"))
    }

    private fun saveDestinationToFirebase(latLng: LatLng) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val destinationRef =
                FirebaseDatabase.getInstance().getReference("UserDestinations").child(it.uid)
            destinationRef.setValue(latLng)
        }
    }
    private fun showDriverOnWaySnackbar() {
        progressDialog.setMessage("Driver Accepted Request hes on the way Do not press back")


        val rootView: View = findViewById(android.R.id.content)
        val snackbar = Snackbar.make(rootView, "Your driver is on the way. Please be ready.", Snackbar.LENGTH_LONG)
        snackbar.setAction("OK") { snackbar.dismiss() }
        snackbar.show()
        listenForDriverArrival()
    }

    private fun listenForDriverArrival() {
        val driverArrivedRef = FirebaseDatabase.getInstance().getReference("DriverArrived")
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        uid?.let {
            driverArrivedRef.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() ) {
                        Log.d("MapsViews", "Driver has arrived.")
                        progressDialog.dismiss()
                        Snackbar.make(findViewById(android.R.id.content), "Your driver has arrived!", Snackbar.LENGTH_LONG).show()
                       driverPage()


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MapsViews", "Failed to check driver arrival.", error.toException())
                }
            })
        }
    }

    private fun driverPage(){
        Log.e("driverPage", "Launching Driver Page",)
        val intent = Intent(this, DriverArrivedOverviewActivity::class.java)
        startActivity(intent)
    }
//    override fun onBackPressed() {
//        // Dismiss the progress dialog if it's showing
//        progressDialog.dismiss()
//
//
//
//
//
//        super.onBackPressed()
//    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }


}