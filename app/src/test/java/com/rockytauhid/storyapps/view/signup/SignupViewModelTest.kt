package com.rockytauhid.storyapps.view.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.rockytauhid.storyapps.data.remote.GeneralResponse
import com.rockytauhid.storyapps.data.repository.UserRepository
import com.rockytauhid.storyapps.utils.DataDummy
import com.rockytauhid.storyapps.model.Result
import com.rockytauhid.storyapps.utils.MainDispatcherRule
import com.rockytauhid.storyapps.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SignupViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var signupViewModel: SignupViewModel
    private val dummyName = DataDummy.generateDummyName()
    private val dummyEmail = DataDummy.generateDummyEmail()
    private val dummyPassword = DataDummy.generateDummyPassword()
    private val dummyGeneralResponse = DataDummy.generateDummySuccessGeneralResponse()

    @Mock
    private lateinit var mockUserRepository: UserRepository

    @Before
    fun setUp() {
        signupViewModel = SignupViewModel(mockUserRepository)
    }

    @Test
    fun `when register Should Not Null and Return Success`() {
        val expectedResponse = MutableLiveData<Result<GeneralResponse>>()
        expectedResponse.value = Result.Success(dummyGeneralResponse)
        `when`(mockUserRepository.register(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )
        val actualResponse =
            signupViewModel.register(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()

        Mockito.verify(mockUserRepository).register(dummyName, dummyEmail, dummyPassword)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertNotNull(actualResponse)
    }

    @Test
    fun `when register Error and Return Error`() {
        val expectedResponse = MutableLiveData<Result<GeneralResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(mockUserRepository.register(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )
        val actualResponse =
            signupViewModel.register(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()

        Mockito.verify(mockUserRepository).register(dummyName, dummyEmail, dummyPassword)
        Assert.assertTrue(actualResponse is Result.Error)
        Assert.assertNotNull(actualResponse)
    }
}