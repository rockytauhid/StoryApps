package com.rockytauhid.storyapps.view.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.rockytauhid.storyapps.R
import com.rockytauhid.storyapps.data.remote.ListStoryItem
import com.rockytauhid.storyapps.databinding.ActivityMapsBinding
import com.rockytauhid.storyapps.model.Result
import com.rockytauhid.storyapps.model.ViewModelFactory
import com.rockytauhid.storyapps.view.login.LoginActivity
import com.rockytauhid.storyapps.view.main.MainActivity
import com.rockytauhid.storyapps.view.new_story.NewStoryActivity
import com.rockytauhid.storyapps.view.welcome.WelcomeActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()

    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getMyLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getMyLocation()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        menu.findItem(R.id.action_map).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.action_add -> {
                val i = Intent(this, NewStoryActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.action_logout -> {
                mapsViewModel.logout()
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                return true
            }
            else -> return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupView()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
        setupViewModel()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupViewModel() {
        mapsViewModel.getToken().observe(this) { token ->
            if (token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                mapsViewModel.getStoriesWithLocation(token).observe(this) { response ->
                    when (response) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(this@MapsActivity, response.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                        is Result.Success -> {
                            showLoading(false)
                            if (response.data != null)
                                addStoriesMarker(response.data.listStory)
                            Toast.makeText(this@MapsActivity, response.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun addStoriesMarker(listStory: List<ListStoryItem>) {
        listStory.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            mMap.addMarker(
                MarkerOptions().position(latLng).title(story.name).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                )
            )
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.apply {
            visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    companion object {
        const val TAG = "MapsActivity"
    }
}