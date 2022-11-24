package com.rockytauhid.storyapps.view.detail_story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.rockytauhid.storyapps.data.remote.StoryResponse
import com.rockytauhid.storyapps.data.repository.StoryRepository
import com.rockytauhid.storyapps.data.repository.UserRepository
import com.rockytauhid.storyapps.model.Result
import com.rockytauhid.storyapps.utils.DataDummy
import com.rockytauhid.storyapps.utils.MainDispatcherRule
import com.rockytauhid.storyapps.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DetailStoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var detailStoryViewModel: DetailStoryViewModel
    private val mockUserRepository: UserRepository = Mockito.mock(UserRepository::class.java)
    private val mockStoryRepository: StoryRepository = Mockito.mock(StoryRepository::class.java)
    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyStoryId = DataDummy.generateDummyStoryId()
    private val dummyStory = DataDummy.generateDummySuccessStoryResponse()
    private val originalOut = System.out

    @Before
    fun setUp() {
        detailStoryViewModel = DetailStoryViewModel(mockUserRepository, mockStoryRepository)
    }

    @Test
    fun `when get Token Should Not Null and Return String`() {
        val expectedResponse = flowOf(dummyToken)
        `when`(mockUserRepository.getToken()).thenReturn(expectedResponse)
        val actualResponse = detailStoryViewModel.getToken().getOrAwaitValue()

        Mockito.verify(mockUserRepository).getToken()
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedResponse.asLiveData().getOrAwaitValue(), actualResponse)
    }

    @Test
    fun `when get Story Should Not Null and Return Success`() {
        val expectedResponse = MutableLiveData<Result<StoryResponse>>()
        expectedResponse.value = Result.Success(dummyStory)
        `when`(mockStoryRepository.getStory(dummyToken, dummyStoryId)).thenReturn(expectedResponse)
        val actualResponse =
            detailStoryViewModel.getStory(dummyToken, dummyStoryId).getOrAwaitValue()

        originalOut.print("Test result: actualResponse.data?.story?.id.toString()")
        Mockito.verify(mockStoryRepository).getStory(dummyToken, dummyStoryId)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertNotNull(actualResponse)
    }

    @Test
    fun `when get Story Error and Return Error`() {
        val expectedResponse = MutableLiveData<Result<StoryResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(mockStoryRepository.getStory(dummyToken, dummyStoryId)).thenReturn(expectedResponse)
        val actualResponse =
            detailStoryViewModel.getStory(dummyToken, dummyStoryId).getOrAwaitValue()

        Mockito.verify(mockStoryRepository).getStory(dummyToken, dummyStoryId)
        Assert.assertTrue(actualResponse is Result.Error)
        Assert.assertNotNull(actualResponse)
    }

    @Test
    fun `set logout Successfully`() = runTest {
        detailStoryViewModel.logout()
        Mockito.verify(mockUserRepository).deleteUser()
    }
}