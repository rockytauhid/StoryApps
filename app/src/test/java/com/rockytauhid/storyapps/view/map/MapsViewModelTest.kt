package com.rockytauhid.storyapps.view.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.rockytauhid.storyapps.data.remote.StoriesResponse
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
class MapsViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var mapsViewModel: MapsViewModel
    private val mockUserRepository: UserRepository = Mockito.mock(UserRepository::class.java)
    private val mockStoryRepository: StoryRepository = Mockito.mock(StoryRepository::class.java)
    private val dummyStories = DataDummy.generateDummySuccessStoriesResponse()
    private val dummyToken = DataDummy.generateDummyToken()

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(mockUserRepository, mockStoryRepository)
    }

    @Test
    fun `when get Token Should Not Null and Return String`() {
        val expectedResponse = flowOf(dummyToken)
        `when`(mockUserRepository.getToken()).thenReturn(expectedResponse)
        val actualResponse = mapsViewModel.getToken().getOrAwaitValue()

        Mockito.verify(mockUserRepository).getToken()
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedResponse.asLiveData().getOrAwaitValue(), actualResponse)
    }

    @Test
    fun `when get Stories With Location Should Not Null and Return Success`() {
        val expectedResponse = MutableLiveData<Result<StoriesResponse>>()
        expectedResponse.value = Result.Success(dummyStories)
        `when`(mockStoryRepository.getStoriesWithLocation(dummyToken)).thenReturn(expectedResponse)
        val actualResponse = mapsViewModel.getStoriesWithLocation(dummyToken).getOrAwaitValue()

        Mockito.verify(mockStoryRepository).getStoriesWithLocation(dummyToken)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(
            dummyStories.listStory.size,
            (actualResponse as Result.Success).data?.listStory?.size
        )
    }

    @Test
    fun `when get Stories With Location Error and Return Error`() {
        val expectedResponse = MutableLiveData<Result<StoriesResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(mockStoryRepository.getStoriesWithLocation(dummyToken)).thenReturn(expectedResponse)
        val actualResponse = mapsViewModel.getStoriesWithLocation(dummyToken).getOrAwaitValue()

        Mockito.verify(mockStoryRepository).getStoriesWithLocation(dummyToken)
        Assert.assertTrue(actualResponse is Result.Error)
        Assert.assertNotNull(actualResponse)
    }

    @Test
    fun `set logout Successfully`() = runTest {
        mapsViewModel.logout()
        Mockito.verify(mockUserRepository).deleteUser()
    }
}