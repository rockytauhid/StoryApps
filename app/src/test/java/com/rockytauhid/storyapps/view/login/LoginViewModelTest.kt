package com.rockytauhid.storyapps.view.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.rockytauhid.storyapps.data.remote.LoginResponse
import com.rockytauhid.storyapps.data.repository.UserRepository
import com.rockytauhid.storyapps.utils.DataDummy
import com.rockytauhid.storyapps.model.Result
import com.rockytauhid.storyapps.utils.MainDispatcherRule
import com.rockytauhid.storyapps.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var loginViewModel: LoginViewModel
    private val dummyEmail = DataDummy.generateDummyEmail()
    private val dummyPassword = DataDummy.generateDummyPassword()
    private val dummyLoginResponse = DataDummy.generateDummySuccessLoginResponse()

    @Mock
    private lateinit var mockUserRepository: UserRepository

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(mockUserRepository)
    }

    @Test
    fun `when login Should Not Null and Return Success`() = runTest {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Success(dummyLoginResponse)
        `when`(mockUserRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()

        Mockito.verify(mockUserRepository).login(dummyEmail, dummyPassword)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertNotNull(actualResponse)
    }

    @Test
    fun `when login Error and Return Error`() = runTest {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(mockUserRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()

        Mockito.verify(mockUserRepository).login(dummyEmail, dummyPassword)
        Assert.assertTrue(actualResponse is Result.Error)
        Assert.assertNotNull(actualResponse)
    }
}