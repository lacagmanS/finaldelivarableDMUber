package com.example.dmuber

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.firebase.geofire.core.GeoHash
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.android.SphericalUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DriverMapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var customerLocation2: LatLng
    private lateinit var customername2: String
    private var customerMarker: Marker? = null
    private val markerList = mutableListOf<Marker>()
    private val markerMap = HashMap<String, Marker>()

    private var proximityLocationCallback: LocationCallback? = null


    private var destinationmarker: Marker? = null

    private lateinit var locationlateinit: LatLng
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private  var location: Location? = null

    private lateinit var onlineRef: DatabaseReference
    private lateinit var currentUserRef: DatabaseReference
    private lateinit var driversLocationRef: DatabaseReference
    private lateinit var geoFire: GeoFire

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
        driversLocationRef =
            FirebaseDatabase.getInstance().getReference("DriverLocationReference")
        currentUserRef = FirebaseDatabase.getInstance().getReference("DriverLocationReference")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        geoFire = GeoFire(driversLocationRef)

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
                 location = locationResult.lastLocation
                location?.let { updateLocation(it) }
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        Log.d("LocationUpdates", "startLocationUpdates() called")
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
        mMap.setOnMarkerClickListener(this)
        val location1 = LatLng(37.422148099068565, -122.08343893289566) // Example destination
        val location2 = LatLng(37.4221, -122.084) // Example nearby driver location



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
        Log.d("update location", "updateLocation() called")
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

                Log.d("online", "onlineeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                startListeningForCarpoolRequests()
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




    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }


    private fun startListeningForCarpoolRequests() {


        Log.d("startListeningForCarpoolRequests", "startListeningForCarpoolRequests() called")
        val requestsRef = FirebaseDatabase.getInstance().getReference("CarpoolRequests")

        requestsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val destinationSnapshot = snapshot.child("destination")
                val latitude =
                    destinationSnapshot.child("latitude").getValue(Double::class.java)
                val longitude =
                    destinationSnapshot.child("longitude").getValue(Double::class.java)
                if (latitude != null && longitude != null) {
                    val customerDestination = LatLng(latitude, longitude)

                    // Get the customer's name from the database
                    val customerUid = snapshot.child("uid").getValue(String::class.java)
                    val customerLocationRef =
                        FirebaseDatabase.getInstance().getReference("CustomerLocationReference")
                            .child(customerUid ?: "")
                    val customersRef = FirebaseDatabase.getInstance().getReference("Customers")
                    customersRef.child(customerUid ?: "")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val customerName =
                                    dataSnapshot.child("fullName").getValue(String::class.java)
                                if (customerName != null) {
                                    Log.d(
                                        "Customerdestination",
                                        "Updating destination to: $customerDestination"
                                    )
                                    addMarkerForRequest(snapshot)
                                    val customerUid =
                                        snapshot.child("uid").getValue(String::class.java)
                                    val customerLocationRef = FirebaseDatabase.getInstance()
                                        .getReference("CustomerLocationReference")
                                        .child(customerUid ?: "")


                                    customerLocationRef.addValueEventListener(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            val geoHash = dataSnapshot.child("g")
                                                .getValue(String::class.java)
                                            geoHash?.let { initiateGeoQuery(it, customerName) }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // Handle error
                                        }
                                    })




                                    Log.d(
                                        "destination marker put",
                                        "putttttttttttttttttttttttttttttttttttttttttttt"
                                    )
                                } else {
                                    // Handle case where customer name is not found
                                    Snackbar.make(
                                        findViewById(android.R.id.content),
                                        "Customer name not found",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle error (if needed)
                                Snackbar.make(
                                    findViewById(android.R.id.content),
                                    "Failed to retrieve customer name",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        })
                }


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle changes to existing requests (if needed)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                removeMarkerForRequest(snapshot)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle moved requests (if needed)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error (if needed)
            }
        })

    }


    private fun initiateGeoQuery(geoFireHash: String, customerName: String) {
        val geoQuery = geoFire.queryAtLocation(
            geoLocationFromGeoHash(geoFireHash),
            1.0
        ) // 1.0 is the radius in kilometers, adjust as needed
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                location?.let {
                    val customerLocation = LatLng(it.latitude, it.longitude)
                    updateCustomerBlipPosition(customerLocation, customerName)
                    Log.d("got here111111111111", "hereeeeeeeeeeeeeeeeeeeeee111: $location")

                }
            }

            override fun onKeyExited(key: String?) {
                // Handle case when the key exits
            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {
                location?.let {
                    val customerLocation = LatLng(it.latitude, it.longitude)
                    updateCustomerBlipPosition(customerLocation, customerName)


                }
            }

            override fun onGeoQueryReady() {
                // All initial data has been loaded and events have been fired
            }

            override fun onGeoQueryError(error: DatabaseError?) {
                // Handle error
            }
        })
    }


    // Function to update blip's position on the map
    private fun updateCustomerBlipPosition(location: LatLng, customerName: String) {
        customerMarker?.remove()

        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher2)

        val desiredWidth = 70
        val desiredHeight = 70

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, false)

        val markerOptions = MarkerOptions().title(customerName)
            .position(location)
            .anchor(0.5f, 0.5f)
            .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))

        customerMarker = mMap.addMarker(markerOptions)

        Log.d("small blip", "blipppppppppppppppppppppppppppppppppppppo: $location")
    }


    private fun geoLocationFromGeoHash(geoHash: String): GeoLocation {
        val location = GeoHash.locationFromHash(geoHash)
        return GeoLocation(location.latitude, location.longitude)
    }

    private fun showLocationRequestAlert(customerName: String, customerLocation: LatLng, p0: Marker) {


        val carpoolRequestsRef = FirebaseDatabase.getInstance().getReference("CarpoolRequests")
        carpoolRequestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUserId != null) {
                    for (childSnapshot in dataSnapshot.children) {
                        val customerId = childSnapshot.child("uid").getValue(String::class.java)
                        val destination = childSnapshot.child("destination")


//                                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
//
//                                if (currentUserId != null) {
//                                    val locationArray = dataSnapshot.child(currentUserId).child("l")
//                                        .getValue(Array<Double>::class.java)
////                                    val driverLatitude = locationArray?.get(0) ?: 0.0
////                                    val driverLongitude = locationArray?.get(1) ?: 0.0


                                    val latitude =
                                        destination.child("latitude").getValue(Double::class.java)
                                    val longitude =
                                        destination.child("longitude").getValue(Double::class.java)
                                    Log.d(
                                        "values",
                                        "customer id $customerId destination:$destination $latitude $longitude"
                                    )
                                    if (customerId != null && latitude != null && longitude != null) {
                                        val destinationslatlng = LatLng(latitude, longitude)


                                        val currentLocation = LatLng(location!!.latitude , location!!.longitude)

                                        val distance =
                                            calculateDistance(currentLocation, destinationslatlng)

                                        Log.d("DistanceCalculation", "Distance between driver and destination: $distance , curerntlo: $currentLocation, destination: $destinationslatlng")




                                        val alertDialogBuilder = AlertDialog.Builder(this@DriverMapsActivity)
                                        alertDialogBuilder.apply {
                                            setTitle("Carpool Request")
                                            setMessage("$customerName is $distance meters away. Do you want to accept this carpool request?")
                                            setPositiveButton("Accept") { dialog, which ->
                                                val uid = customerId


                                                if (uid != null && latitude != null && longitude != null) {

                                                    val customerLocation = LatLng(latitude,longitude)

                                                    val currentTime = getCurrentTime()
                                                    Log.d("CarpoolRequest", "Current Time: $currentTime")

                                                    val customerLocationString = "$latitude,$longitude"
                                                    Log.d("CarpoolRequest", "Customer Location: $customerLocationString")

                                                    val driverID = FirebaseAuth.getInstance().currentUser!!.uid
                                                    Log.d("CarpoolRequest", "Driver ID: $driverID")

                                                    val acceptedRequestData = HashMap<String, Any>()
                                                    acceptedRequestData["customerid"] = uid
                                                    Log.d("CarpoolRequest", "Customer ID: $uid")

                                                    acceptedRequestData["CustomerLocation"] = customerLocationString
                                                    Log.d("CarpoolRequest", "Customer Location: $customerLocationString")

                                                    acceptedRequestData["DriverLocation"] = currentLocation
                                                    Log.d("CarpoolRequest", "Driver Location: $currentLocation")

                                                    acceptedRequestData["CustomerDestination"] = customerLocationString
                                                    Log.d("CarpoolRequest", "Customer Destination: $customerLocationString")

                                                    acceptedRequestData["DriverID"] = driverID
                                                    Log.d("CarpoolRequest", "Driver ID: $driverID")

                                                    acceptedRequestData["Time"] = currentTime
                                                    Log.d("CarpoolRequest", "Time: $currentTime")

                                                    val acceptedBookingsRef = FirebaseDatabase.getInstance().getReference("AcceptedCarpoolRequest").child(uid)
                                                    acceptedBookingsRef.setValue(acceptedRequestData)
                                                        .addOnSuccessListener {

                                                            Snackbar.make(findViewById(android.R.id.content), "Booking accepted and added to AcceptedCarpoolRequest", Snackbar.LENGTH_LONG).show()
                                                            monitorProximityToCustomer(customerLocation, customerId)
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.e("CarpoolRequest", "Error adding booking to AcceptedCarpoolRequest: ${e.message}", e)
                                                            Snackbar.make(findViewById(android.R.id.content), "Failed to add booking: ${e.message}", Snackbar.LENGTH_LONG).show()
                                                        }


                                                }
                                                else{
                                                    Snackbar.make(findViewById(android.R.id.content), "values are null", Snackbar.LENGTH_LONG).show()

                                                }







                                            }
                                            setNegativeButton("Decline") { dialog, which ->
                                                Snackbar.make(findViewById(android.R.id.content), "declined", Snackbar.LENGTH_LONG).show()

                                            }
                                        }
                                        val alertDialog = alertDialogBuilder.create()
                                        alertDialog.show()


                                    } else {
                                        Snackbar.make(
                                            findViewById(android.R.id.content),
                                            "error3",
                                            Snackbar.LENGTH_LONG
                                        ).show()

                                    }
//                                }


                            }







                }}
            override fun onCancelled(databaseError: DatabaseError) {
                Snackbar.make(findViewById(android.R.id.content), "error2", Snackbar.LENGTH_LONG).show()
                Log.d("title " ,"${p0.title}")
                p0.setTitle("")
                Log.d("title  after" ,"${p0.title}")


            }})

    }






    private fun calculateDistance(location1: LatLng, location2: LatLng): Double {
        return SphericalUtil.computeDistanceBetween(location1, location2)
    }


    private fun resizeBitmap(imageResId: Int, width: Int, height: Int): Bitmap {
        val originalBitmap = BitmapFactory.decodeResource(resources, imageResId)
        return Bitmap.createScaledBitmap(originalBitmap, width, height, false)
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        return dateFormat.format(currentTime)
    }


    override fun onMarkerClick(p0: Marker): Boolean {

        val namecheck = p0.title ?: ""
        if (namecheck.endsWith("Destination")) {


            val namewithdestination = p0.title ?: ""
            val charsToKeep = namewithdestination.length - 14
            val Name = if (charsToKeep >= 0) namewithdestination.substring(0, charsToKeep) else ""
            showLocationRequestAlert(Name, p0.position, p0)


            return false
        } else if (p0 == null) {
            p0.remove()


            Snackbar.make(
                findViewById(android.R.id.content),
                "This carpool request has been cancelled by the user",
                Snackbar.LENGTH_LONG
            ).show()
            return false
        } else {
            Snackbar.make(
                findViewById(android.R.id.content),
                "This is a customer location marker for: ${p0.title}, click on destination marker to accept requests",
                Snackbar.LENGTH_LONG
            ).show()

            return false
        }

    }


    private fun monitorProximityToCustomer(customerLatLng: LatLng, customerId: String) {
        proximityLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let { driverLocation ->
                    val driverLatLng = LatLng(driverLocation.latitude, driverLocation.longitude)
                    val distance = SphericalUtil.computeDistanceBetween(driverLatLng, customerLatLng)

                    if (distance < 5) {
                        Log.d("Proximity", "Driver is within 5 meters of the customer. Success!")
                        val nodeRef = FirebaseDatabase.getInstance().getReference("DriverArrived")
                        val arrivalInfo = mapOf(
                            "driverId" to FirebaseAuth.getInstance().currentUser?.uid,
                            "customerId" to customerId,
                            "customerLatLng" to customerLatLng,
                            "arrivalTime" to getCurrentTime()
                        )
                        val driverArrivedRef = nodeRef.child(customerId)
                        driverArrivedRef.setValue(arrivalInfo).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("Proximity", "DriverArrived node created successfully")
                            } else {
                                Log.e("Proximity", "Failed to create DriverArrived node", task.exception)
                            }
                        }
                        fusedLocationProviderClient.removeLocationUpdates(this)
                    }
                }
            }
        }

        // Request updates with the proximityLocationCallback
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                proximityLocationCallback!!,
                Looper.getMainLooper()
            )
        }
    }

    private fun addMarkerForRequest(snapshot: DataSnapshot) {
        val customerUid = snapshot.key ?: return  // Assuming UID is the key for each request
        val destinationSnapshot = snapshot.child("destination")
        val latitude = destinationSnapshot.child("latitude").getValue(Double::class.java)
        val longitude = destinationSnapshot.child("longitude").getValue(Double::class.java)
        if (latitude != null && longitude != null) {
            val customerDestination = LatLng(latitude, longitude)
            val marker = mMap.addMarker(
                MarkerOptions().position(customerDestination).title("Destination")
            )
            marker?.let {
                markerMap[customerUid] = it
            }
        }
    }

    private fun removeMarkerForRequest(snapshot: DataSnapshot) {
        val customerUid = snapshot.key ?: return
        markerMap[customerUid]?.remove() // Remove the marker from the map
        markerMap.remove(customerUid) // Remove the entry from the HashMap
    }
}



