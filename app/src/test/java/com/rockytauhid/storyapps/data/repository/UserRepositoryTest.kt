package com.rockytauhid.storyapps.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rockytauhid.storyapps.data.remote.MockApiService
import com.rockytauhid.storyapps.data.remote.ApiService
import com.rockytauhid.storyapps.utils.DataDummy
import com.rockytauhid.storyapps.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class UserRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private val mockApi: ApiService = MockApiService()
    private val dummyName = DataDummy.generateDummyName()
    private val dummyEmail = DataDummy.generateDummyEmail()
    private val dummyPassword = DataDummy.generateDummyPassword()

    @Test
    fun `when register Should Not Null and Return Success`() = runTest {
        val expectedResponse = DataDummy.generateDummySuccessGeneralResponse()
        val actualResponse = mockApi.register(dummyName, dummyEmail, dummyPassword)

        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `when login Should Not Null and Return Login Response`() = runTest {
        val expectedResponse = DataDummy.generateDummySuccessLoginResponse()
        val actualResponse = mockApi.login(dummyEmail, dummyPassword)

        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedResponse, actualResponse)
    }
}