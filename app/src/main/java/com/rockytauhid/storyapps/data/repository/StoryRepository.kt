package com.rockytauhid.storyapps.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.rockytauhid.storyapps.data.StoryRemoteMediator
import com.rockytauhid.storyapps.data.database.StoryDatabase
import com.rockytauhid.storyapps.data.database.StoryModel
import com.rockytauhid.storyapps.data.remote.ApiService
import com.rockytauhid.storyapps.data.remote.GeneralResponse
import com.rockytauhid.storyapps.data.remote.StoriesResponse
import com.rockytauhid.storyapps.data.remote.StoryResponse
import com.rockytauhid.storyapps.helper.wrapEspressoIdlingResource
import com.rockytauhid.storyapps.model.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): LiveData<PagingData<StoryModel>> {
        wrapEspressoIdlingResource {
            return Pager(
                config = PagingConfig(
                    pageSize = 10
                ),
                remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
                pagingSourceFactory = {
                    storyDatabase.storyDao().getAll()
                }
            ).liveData
        }
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<StoriesResponse>> = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading())
            try {
                val client = apiService.getStories("Bearer $token", size = 20, location = 1)
                if (!client.error) {
                    emit(Result.Success(client))
                } else {
                    emit(Result.Error(client.message))
                    Log.e(TAG, client.message)
                }

            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun getStory(token: String, id: String): LiveData<Result<StoryResponse>> = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading())
            try {
                val client = apiService.getStory("Bearer $token", id)
                if (!client.error) {
                    emit(Result.Success(client))
                } else {
                    emit(Result.Error(client.message))
                    Log.e(TAG, client.message)
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun addStory(
        token: String,
        imageFile: MultipartBody.Part,
        description: RequestBody,
        latitude: Float,
        longitude: Float
    ): LiveData<Result<GeneralResponse>> = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading())
            try {
                val client =
                    apiService.addStory(
                        "Bearer $token",
                        imageFile,
                        description,
                        latitude,
                        longitude
                    )
                if (!client.error) {
                    emit(Result.Success(client))
                } else {
                    emit(Result.Error(client.message))
                    Log.e(TAG, client.message)
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
                Log.e(TAG, e.message.toString())
            }
        }
    }

    companion object {
        private const val TAG = "StoryRepository"
        private var INSTANCE: StoryRepository? = null
        fun getInstance(storyDatabase: StoryDatabase, apiService: ApiService): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                StoryRepository(storyDatabase, apiService).also {
                    INSTANCE = it
                }
            }
        }
    }
}