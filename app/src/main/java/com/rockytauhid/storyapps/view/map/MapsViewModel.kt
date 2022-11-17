package com.rockytauhid.storyapps.view.map

import androidx.lifecycle.*
import com.rockytauhid.storyapps.data.repository.StoryRepository
import com.rockytauhid.storyapps.data.repository.UserRepository
import kotlinx.coroutines.launch

class MapsViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getToken(): LiveData<String> {
        return userRepository.getToken().asLiveData()
    }

    fun getStoriesWithLocation(token: String) = storyRepository.getStoriesWithLocation(token)

    fun logout() = viewModelScope.launch {
        userRepository.deleteUser()
    }
}