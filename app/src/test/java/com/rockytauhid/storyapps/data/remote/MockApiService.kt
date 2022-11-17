package com.rockytauhid.storyapps.data.remote

import com.rockytauhid.storyapps.utils.DataDummy
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MockApiService : ApiService {

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): GeneralResponse {
        return DataDummy.generateDummySuccessGeneralResponse()
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        return DataDummy.generateDummySuccessLoginResponse()
    }

    override suspend fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float,
        lon: Float
    ): GeneralResponse {
        return DataDummy.generateDummySuccessGeneralResponse()
    }

    override suspend fun getStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int?
    ): StoriesResponse {
        return DataDummy.generateDummySuccessStoriesResponse()
    }

    override suspend fun getStory(token: String, page: String): StoryResponse {
        return DataDummy.generateDummySuccessStoryResponse()
    }
}