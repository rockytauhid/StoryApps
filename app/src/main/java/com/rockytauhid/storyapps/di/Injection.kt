package com.rockytauhid.storyapps.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rockytauhid.storyapps.data.database.StoryDatabase
import com.rockytauhid.storyapps.data.local.UserPreferences
import com.rockytauhid.storyapps.data.remote.ApiConfig
import com.rockytauhid.storyapps.data.repository.StoryRepository
import com.rockytauhid.storyapps.data.repository.UserRepository

object Injection {
    fun provideUserRepository(dataStore: DataStore<Preferences>): UserRepository {
        val userPreferences = UserPreferences.getInstance(dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(userPreferences, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(database, apiService)
    }
}