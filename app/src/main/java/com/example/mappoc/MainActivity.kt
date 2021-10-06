package com.example.mappoc

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.mappoc.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import kotlinx.coroutines.launch
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permission_request_code = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mainActivityBinding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        initUi()

        fetchLocation()

    }

    private fun initUi() {
        mainActivityBinding.apply {
            with(btnCancel) {
                makeTextUnderLine()
                onOneClick {
                    finish()
                }
            }

            btnLocateMe.onOneClick { fetchLocation() }

        }


    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        //check if permissions are given or not

        checkLocPermission() {
            showToast("Locations are Enabled")
        }

        val lastLocation = fusedLocationProviderClient.lastLocation
        lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                showToast("${currentLocation.latitude}, ${currentLocation.longitude}")

                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

                uiScope.launch {
                    val gMap = mapFragment.awaitMap()
                    gMap.awaitMapLoad()

                    setLocationOnMap(gMap)

                }


            }
        }

    }

    private fun setLocationOnMap(gMap: GoogleMap) {
        val latLong = LatLng(currentLocation.latitude, currentLocation.longitude)
        gMap.apply {
            addMarker {
                position(latLong)
                title("I am Here...")
            }
            animateCamera(CameraUpdateFactory.newLatLng(latLong))
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 20f))
        }

    }

    private fun checkLocPermission(locPermissionsEnabled: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                permission_request_code
            )
        } else {
            locPermissionsEnabled.invoke()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permission_request_code -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation()
                }
            }
        }
    }


}