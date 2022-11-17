package com.rockytauhid.storyapps.utils

import com.rockytauhid.storyapps.data.database.StoryModel
import com.rockytauhid.storyapps.data.remote.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime

object DataDummy {
    fun generateDummyToken(): String {
        return dummyToken
    }

    fun generateDummyStoryId(): String {
        return dummyStoryId
    }

    fun generateDummyName(): String {
        return dummyName
    }

    fun generateDummyEmail(): String {
        return dummyEmail
    }

    fun generateDummyPassword(): String {
        return dummyPassword
    }

    fun generateDummySuccessLoginResponse(): LoginResponse {
        val loginResult = LoginResult(
            dummyName,
            dummyUserId,
            dummyToken
        )
        return LoginResponse(loginResult, false, "success")
    }

    fun generateDummyPhoto(): MultipartBody.Part {
        return MultipartBody.Part.createFormData("DummyPhotoUrl", dummyPhotoURL)
    }

    fun generateDummyDescription(): RequestBody {
        return dummyDescription.toRequestBody("text/plain".toMediaType())
    }

    fun generateDummyLatitude(): Float {
        return dummyLatitude.toFloat()
    }

    fun generateDummyLongitude(): Float {
        return dummyLongitude.toFloat()
    }

    fun generateDummySuccessGeneralResponse(): GeneralResponse {
        return GeneralResponse(false, "success")
    }

    fun generateDummySuccessStoriesResponse(): StoriesResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "$dummyStoryId$i",
                dummyPhotoURL,
                LocalDateTime.now().toString(),
                "$dummyName $i",
                "$dummyDescription $i",
                dummyLatitude,
                dummyLongitude
            )
            items.add(story)
        }
        return StoriesResponse(items, false, "success")
    }

    fun generateDummyListStoryModel(): List<StoryModel> {
        val items: MutableList<StoryModel> = arrayListOf()
        for (i in 0..100) {
            val story = StoryModel(
                "$dummyStoryId$i",
                dummyPhotoURL,
                LocalDateTime.now().toString(),
                "$dummyName $i",
                "$dummyDescription $i",
                dummyLatitude,
                dummyLongitude
            )
            items.add(story)
        }
        return items
    }

    fun generateDummySuccessStoryResponse(): StoryResponse {
        return StoryResponse(
            false,
            "success",
            StoryItem(
                dummyStoryId,
                dummyName,
                dummyDescription,
                dummyPhotoURL,
                LocalDateTime.now().toString(),
                dummyLatitude,
                dummyLongitude
            )
        )
    }

    private const val dummyStoryId: String = "story-qwerty1234567890"
    private const val dummyUserId: String = "user-qwerty1234567890"
    private const val dummyName: String = "DummyName"
    private const val dummyEmail: String = "dummy@mail.com"
    private const val dummyPassword: String = "p@55w0rd"
    private const val dummyToken: String =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLW8ybFlUbTd4ZkhnN0phd2giLCJpYXQiOjE2NjgyODQyODd9.y-Q2wevn_ZbvA-N-5MxYaJFUFABlW-aiVKMxCJ06TeQ"
    private const val dummyDescription: String = "Dummy description"
    private const val dummyPhotoURL: String = "https://picsum.photos/200/300"
    private const val dummyLatitude: Double = 37.422244492756164
    private const val dummyLongitude: Double = -122.08406823708735
}