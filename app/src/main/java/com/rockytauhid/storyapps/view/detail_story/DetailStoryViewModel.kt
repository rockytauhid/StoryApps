package com.rockytauhid.storyapps.view.detail_story

import androidx.lifecycle.*
import com.rockytauhid.storyapps.data.repository.StoryRepository
import com.rockytauhid.storyapps.data.repository.UserRepository
import kotlinx.coroutines.launch

class DetailStoryViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            userRepository.deleteUser()
        }
    }

    fun getToken(): LiveData<String> {
        return userRepository.getToken().asLiveData()
    }

    fun getStory(token: String, id: String) = storyRepository.getStory(token, id)
}