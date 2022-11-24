package com.rockytauhid.storyapps.view.new_story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.rockytauhid.storyapps.data.remote.GeneralResponse
import com.rockytauhid.storyapps.data.repository.StoryRepository
import com.rockytauhid.storyapps.data.repository.UserRepository
import com.rockytauhid.storyapps.utils.DataDummy
import com.rockytauhid.storyapps.model.Result
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
class NewStoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var newStoryViewModel: NewStoryViewModel
    private val mockUserRepository: UserRepository = Mockito.mock(UserRepository::class.java)
    private val mockStoryRepository: StoryRepository = Mockito.mock(StoryRepository::class.java)
    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyPhoto = DataDummy.generateDummyPhoto()
    private val dummyDesc = DataDummy.generateDummyDescription()
    private val dummyLat = DataDummy.generateDummyLatitude()
    private val dummyLon = DataDummy.generateDummyLongitude()
    private val dummySuccessGeneralResponse = DataDummy.generateDummySuccessGeneralResponse()

    @Before
    fun setUp() {
        newStoryViewModel = NewStoryViewModel(mockUserRepository, mockStoryRepository)
    }

    @Test
    fun `when get Token Should Not Null and Return String`() {
        val expectedResponse = flowOf(dummyToken)
        `when`(mockUserRepository.getToken()).thenReturn(expectedResponse)
        val actualResponse = newStoryViewModel.getToken().getOrAwaitValue()

        Mockito.verify(mockUserRepository).getToken()
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedResponse.asLiveData().getOrAwaitValue(), actualResponse)
    }

    @Test
    fun `when add New Story Should Not Null and Return Success`() {
        val expectedResult = MutableLiveData<Result<GeneralResponse>>()
        expectedResult.value = Result.Success(dummySuccessGeneralResponse)
        `when`(
            mockStoryRepository.addStory(
                dummyToken,
                dummyPhoto,
                dummyDesc,
                dummyLat,
                dummyLon
            )
        ).thenReturn(
            expectedResult
        )
        val actualResponse =
            newStoryViewModel.addStory(dummyToken, dummyPhoto, dummyDesc, dummyLat, dummyLon)
                .getOrAwaitValue()

        Mockito.verify(mockStoryRepository)
            .addStory(dummyToken, dummyPhoto, dummyDesc, dummyLat, dummyLon)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertNotNull(actualResponse)
    }

    @Test
    fun `set logout Successfully`() = runTest {
        newStoryViewModel.logout()
        Mockito.verify(mockUserRepository).deleteUser()
    }
}