package com.rockytauhid.storyapps.view.signup

import androidx.lifecycle.ViewModel
import com.rockytauhid.storyapps.data.repository.UserRepository

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun register(name: String, email: String, password: String) =
        userRepository.register(name, email, password)
}