package com.rockytauhid.storyapps.view.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.rockytauhid.storyapps.data.remote.LoginResponse
import com.rockytauhid.storyapps.data.repository.UserRepository
import com.rockytauhid.storyapps.utils.DataDummy
import com.rockytauhid.storyapps.model.Result
import com.rockytauhid.storyapps.utils.MainDispatcherRule
import com.rockytauhid.storyapps.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyEmail = DataDummy.generateDummyEmail()
    private val dummyPassword = DataDummy.generateDummyPassword()
    private val dummyLoginResult = DataDummy.generateDummyLoginResult()
    private val dummyLoginResponse = DataDummy.generateDummySuccessLoginResponse()

    @Mock
    private lateinit var mockUserRepository: UserRepository

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(mockUserRepository)
    }

    @Test
    fun `set User Successfully`() = runTest {
        loginViewModel.setUser(dummyLoginResult)
        Mockito.verify(mockUserRepository).setUser(dummyLoginResult)
    }

    @Test
    fun `when get Token Should Not Null and Return String`() {
       val expectedResponse = flowOf(dummyToken)
        `when`(mockUserRepository.getToken()).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.getToken().getOrAwaitValue()

        Mockito.verify(mockUserRepository).getToken()
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedResponse.asLiveData().getOrAwaitValue(), actualResponse)
    }

    @Test
    fun `when login Should Not Null and Return Success`() {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Success(dummyLoginResponse)
        `when`(mockUserRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()

        Mockito.verify(mockUserRepository).login(dummyEmail, dummyPassword)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertNotNull(actualResponse)
    }

    @Test
    fun `when login Error and Return Error`() {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(mockUserRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()

        Mockito.verify(mockUserRepository).login(dummyEmail, dummyPassword)
        Assert.assertTrue(actualResponse is Result.Error)
        Assert.assertNotNull(actualResponse)
    }
}