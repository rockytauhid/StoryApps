package com.rockytauhid.storyapps.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.rockytauhid.storyapps.R
import com.rockytauhid.storyapps.databinding.ActivityLoginBinding
import com.rockytauhid.storyapps.model.ViewModelFactory
import com.rockytauhid.storyapps.view.main.MainActivity
import com.rockytauhid.storyapps.model.Result
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
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
        supportActionBar?.hide()

        if (!intent.getStringExtra("email").isNullOrEmpty()) {
            binding.edLoginEmail.setText(intent.getStringExtra("email"))
        }
        if (!intent.getStringExtra("password").isNullOrEmpty()) {
            binding.edLoginPassword.setText(intent.getStringExtra("password"))
        }
    }

    private fun setupViewModel() {
        loginViewModel.getToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.submitLoginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = getString(R.string.enter_email)
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.enter_password)
                }
                else -> {
                    runGif()
                    login(email, password)
                }
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.submitLoginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 500
        }.start()
    }

    private fun login(email: String, password: String) {
        loginViewModel.login(email, password).observe(this) { response ->
            if (response != null) {
                when (response) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        showLoading(false)
                        if (response.data != null)
                            loginViewModel.setUser(response.data.loginResult)
                        openMainPage()
                        Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun openMainPage() {
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    private fun runGif() {
        val imageView: ImageView = findViewById(R.id.imageView)
        Glide.with(this@LoginActivity).load(R.drawable.image_login).into(imageView)
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
}