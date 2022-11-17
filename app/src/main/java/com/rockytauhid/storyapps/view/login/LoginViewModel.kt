package com.rockytauhid.storyapps.view.login

import androidx.lifecycle.*
import com.rockytauhid.storyapps.data.remote.LoginResult
import com.rockytauhid.storyapps.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository,) : ViewModel() {

    fun getToken(): LiveData<String> {
        return userRepository.getToken().asLiveData()
    }

    fun login(email: String, password: String) = userRepository.login(email, password)

    fun setUser(loginResult: LoginResult) {
        viewModelScope.launch {
            userRepository.setUser(loginResult)
        }
    }
}