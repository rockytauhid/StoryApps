package com.rockytauhid.storyapps.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.rockytauhid.storyapps.R
import com.rockytauhid.storyapps.databinding.ActivitySignupBinding
import com.rockytauhid.storyapps.model.Result
import com.rockytauhid.storyapps.model.ViewModelFactory
import com.rockytauhid.storyapps.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private val signupViewModel: SignupViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
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
    }

    private fun setupAction() {
        binding.submitSignupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = getString(R.string.enter_name)
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = getString(R.string.enter_email)
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.enter_password)
                }
                else -> {
                    register(name, email, password)
                }
            }
        }
    }

    private fun register(name: String, email: String, password: String) {
        signupViewModel.register(name, email, password).observe(this) { response ->
            when (response) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this@SignupActivity, response.message, Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    showLoading(false)
                    openLoginPage()
                    Toast.makeText(this@SignupActivity, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openLoginPage() {
        val i = Intent(this, LoginActivity::class.java)
        i.putExtra("email", binding.edRegisterEmail.text.toString())
        i.putExtra("password", binding.edRegisterPassword.text.toString())
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
        finish()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.submitSignupButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
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
}