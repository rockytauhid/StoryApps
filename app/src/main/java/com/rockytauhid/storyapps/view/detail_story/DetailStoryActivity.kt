package com.rockytauhid.storyapps.view.detail_story

import android.content.Intent
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.rockytauhid.storyapps.R
import com.rockytauhid.storyapps.data.remote.StoryItem
import com.rockytauhid.storyapps.databinding.ActivityDetailStoryBinding
import com.rockytauhid.storyapps.helper.withDateFormat
import com.rockytauhid.storyapps.model.Result
import com.rockytauhid.storyapps.model.ViewModelFactory
import com.rockytauhid.storyapps.view.login.LoginActivity
import com.rockytauhid.storyapps.view.main.MainActivity
import com.rockytauhid.storyapps.view.map.MapsActivity
import com.rockytauhid.storyapps.view.new_story.NewStoryActivity
import com.rockytauhid.storyapps.view.welcome.WelcomeActivity
import java.io.IOException
import java.util.*

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var storyItem: StoryItem
    private lateinit var binding: ActivityDetailStoryBinding

    private val storyViewModel: DetailStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
                storyViewModel.logout()
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
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
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
        showLoading(true)

        storyViewModel.getToken().observe(this) { token ->
            if (token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                val storyId = intent.getStringExtra(STORY_ID)
                if (!storyId.isNullOrEmpty()) {
                    storyViewModel.getStory(token, storyId).observe(this) { response ->
                        when (response) {
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Error -> {
                                showLoading(false)
                                Toast.makeText(
                                    this@DetailStoryActivity,
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Result.Success -> {
                                if (response.data != null) {
                                    storyItem = response.data.story
                                    binding.apply {
                                        Glide.with(applicationContext)
                                            .load(storyItem.photoUrl)
                                            .into(ivItemPhoto)

                                        tvDetailName.text = storyItem.name
                                        tvDetailCreatedAt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                            storyItem.createdAt.withDateFormat(TimeZone.getDefault().id)
                                        else
                                            storyItem.createdAt.take(10)
                                        tvDetailDescription.text = storyItem.description

                                        if (storyItem.lat == 0.0 && storyItem.lon == 0.0)
                                            tvDetailLocation.text = resources.getString(R.string.location_not_found)
                                        else
                                            tvDetailLocation.text = getAddressName(storyItem.lat, storyItem.lon)

                                    }
                                }
                                showLoading(false)
                                Toast.makeText(
                                    this@DetailStoryActivity,
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@DetailStoryActivity,
                        "Failed to get story details",
                        Toast.LENGTH_SHORT
                    ).show()
                    openMainPage()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@DetailStoryActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
                Log.d(TAG, "getAddressName: $addressName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private fun openMainPage() {
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
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
        const val TAG = "DetailStoryActivity"
        const val STORY_ID = "story_id"
    }
}