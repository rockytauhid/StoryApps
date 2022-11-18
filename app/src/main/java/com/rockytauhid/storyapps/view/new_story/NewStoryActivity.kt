package com.rockytauhid.storyapps.view.new_story

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rockytauhid.storyapps.R
import com.rockytauhid.storyapps.model.Result
import com.rockytauhid.storyapps.databinding.ActivityNewStoryBinding
import com.rockytauhid.storyapps.helper.reduceFileImage
import com.rockytauhid.storyapps.helper.rotateBitmap
import com.rockytauhid.storyapps.helper.uriToFile
import com.rockytauhid.storyapps.model.ViewModelFactory
import com.rockytauhid.storyapps.view.camera.CameraActivity
import com.rockytauhid.storyapps.view.login.LoginActivity
import com.rockytauhid.storyapps.view.main.MainActivity
import com.rockytauhid.storyapps.view.map.MapsActivity
import com.rockytauhid.storyapps.view.welcome.WelcomeActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val newStoryViewModel: NewStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var imageFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture") as File
            }

            imageFile = myFile

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.ivPreview.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@NewStoryActivity)
            imageFile = myFile
            binding.ivPreview.setImageURI(selectedImg)
        }
    }

    private var myLocation: Location? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getMyLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getMyLastLocation()
            }
            else -> {
                binding.locationSwitch.isChecked = false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.not_permitted),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        menu.findItem(R.id.action_add).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.action_map -> {
                val i = Intent(this, MapsActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.action_logout -> {
                newStoryViewModel.logout()
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                true
            }
            else -> true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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

    private fun setupAction() {
        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && myLocation == null)
                getMyLastLocation()
        }
        binding.buttonAdd.setOnClickListener { addStory() }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                Log.i("Location", location?.latitude.toString())
                if (location != null) {
                    myLocation = location
                } else {
                    Toast.makeText(
                        this@NewStoryActivity,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun addStory() {
        if (imageFile != null) {
            newStoryViewModel.getToken().observe(this) { token ->
                if (token.isEmpty()) {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                } else {
                    val file = reduceFileImage(imageFile as File)
                    val descBody = binding.edAddDescription.text.toString()
                        .toRequestBody("text/plain".toMediaType())
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )

                    val lat = (myLocation?.latitude.takeUnless { it == null } ?: 0.0).toFloat()
                    val lon = (myLocation?.longitude.takeUnless { it == null } ?: 0.0).toFloat()

                    newStoryViewModel.addStory(
                        token, imageMultipart, descBody, lat, lon
                    ).observe(this) { response ->
                        when (response) {
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Error -> {
                                showLoading(false)
                                Toast.makeText(
                                    this@NewStoryActivity, response.message, Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Result.Success -> {
                                showLoading(false)
                                openMainPage()
                                Toast.makeText(
                                    this@NewStoryActivity, response.message, Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(
                this@NewStoryActivity,
                getString(R.string.choose_picture),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun playAnimation() {
        val imagePreview =
            ObjectAnimator.ofFloat(binding.ivPreview, View.ALPHA, 1f).setDuration(500)
        val cameraXButton =
            ObjectAnimator.ofFloat(binding.cameraXButton, View.ALPHA, 1f).setDuration(500)
        val galleryButton =
            ObjectAnimator.ofFloat(binding.galleryButton, View.ALPHA, 1f).setDuration(500)
        val descriptionEditTextLayout =
            ObjectAnimator.ofFloat(binding.descriptionEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val locationSwitch =
            ObjectAnimator.ofFloat(binding.locationSwitch, View.ALPHA, 1f)
                .setDuration(500)
        val addButton = ObjectAnimator.ofFloat(binding.buttonAdd, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(cameraXButton, galleryButton)
        }

        AnimatorSet().apply {
            playSequentially(
                imagePreview,
                together,
                descriptionEditTextLayout,
                locationSwitch,
                addButton
            )
            startDelay = 500
        }.start()
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

    private fun openMainPage() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}