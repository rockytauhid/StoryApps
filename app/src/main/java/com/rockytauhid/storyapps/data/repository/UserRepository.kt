package com.rockytauhid.storyapps.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.rockytauhid.storyapps.data.local.UserPreferences
import com.rockytauhid.storyapps.model.Result
import com.rockytauhid.storyapps.data.remote.ApiService
import com.rockytauhid.storyapps.data.remote.GeneralResponse
import com.rockytauhid.storyapps.data.remote.LoginResponse
import com.rockytauhid.storyapps.data.remote.LoginResult
import com.rockytauhid.storyapps.helper.wrapEspressoIdlingResource
import java.lang.Exception

class UserRepository(
    private val userPreferences: UserPreferences,
    private val apiService: ApiService
) {

    fun register(name: String, email: String, password: String): LiveData<Result<GeneralResponse>> =
        liveData {
            wrapEspressoIdlingResource {
                emit(Result.Loading())
                try {
                    val client = apiService.register(name, email, password)
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

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading())
            try {
                val client = apiService.login(email, password)
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

    suspend fun setUser(loginResult: LoginResult) = userPreferences.setUser(loginResult)

    fun getToken() = userPreferences.getToken()

    suspend fun deleteUser() = userPreferences.deleteUser()

    companion object {
        private const val TAG = "UserRepository"
        private var INSTANCE: UserRepository? = null
        fun getInstance(userPreferences: UserPreferences, apiService: ApiService): UserRepository {
            return INSTANCE ?: synchronized(this) {
                UserRepository(userPreferences, apiService).also {
                    INSTANCE = it
                }
            }
        }
    }

}

