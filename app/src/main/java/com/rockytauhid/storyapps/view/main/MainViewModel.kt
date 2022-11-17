package com.rockytauhid.storyapps.view.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rockytauhid.storyapps.data.database.StoryModel
import com.rockytauhid.storyapps.data.repository.StoryRepository
import com.rockytauhid.storyapps.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getToken(): LiveData<String> {
        return userRepository.getToken().asLiveData()
    }

    fun getStories(token: String): LiveData<PagingData<StoryModel>> =
        storyRepository.getStories(token).cachedIn(viewModelScope)

    fun logout() = viewModelScope.launch {
        userRepository.deleteUser()
    }
}