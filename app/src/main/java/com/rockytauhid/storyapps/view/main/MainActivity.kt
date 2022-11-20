package com.rockytauhid.storyapps.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rockytauhid.storyapps.R
import com.rockytauhid.storyapps.adapter.LoadingStateAdapter
import com.rockytauhid.storyapps.adapter.StoryListAdapter
import com.rockytauhid.storyapps.databinding.ActivityMainBinding
import com.rockytauhid.storyapps.model.ViewModelFactory
import com.rockytauhid.storyapps.view.login.LoginActivity
import com.rockytauhid.storyapps.view.map.MapsActivity
import com.rockytauhid.storyapps.view.new_story.NewStoryActivity
import com.rockytauhid.storyapps.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyListAdapter: StoryListAdapter

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStories.layoutManager = LinearLayoutManager(this)

        setupView()
        setupViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        menu.findItem(R.id.action_home).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
                mainViewModel.logout()
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                return true
            }
            else -> return true
        }
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
        mainViewModel.getToken().observe(this) { token ->
            if (token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                storyListAdapter = StoryListAdapter()

                mainViewModel.getStories(token).observe(this) {
                    storyListAdapter.submitData(lifecycle, it)
                }

                binding.rvStories.adapter = storyListAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        storyListAdapter.retry()
                    }
                )
            }
        }
    }
}