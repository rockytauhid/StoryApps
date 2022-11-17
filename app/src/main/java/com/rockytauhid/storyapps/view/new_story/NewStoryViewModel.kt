package com.rockytauhid.storyapps.view.new_story

import androidx.lifecycle.*
import com.rockytauhid.storyapps.data.repository.StoryRepository
import com.rockytauhid.storyapps.data.repository.UserRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NewStoryViewModel(
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

    fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float,
        lon: Float
    ) = storyRepository.addStory(token, file, description, lat, lon)
}