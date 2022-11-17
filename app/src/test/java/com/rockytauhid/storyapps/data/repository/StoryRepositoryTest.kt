package com.rockytauhid.storyapps.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.rockytauhid.storyapps.data.database.StoryDatabase
import com.rockytauhid.storyapps.data.remote.*
import com.rockytauhid.storyapps.model.Result
import com.rockytauhid.storyapps.utils.DataDummy
import com.rockytauhid.storyapps.utils.MainDispatcherRule
import com.rockytauhid.storyapps.utils.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var storyRepository: StoryRepository
    private val mockApi: ApiService = MockApiService()

    @Mock
    private val mockDb: StoryDatabase = Mockito.mock(StoryDatabase::class.java)
    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyPhoto = DataDummy.generateDummyPhoto()
    private val dummyDesc = DataDummy.generateDummyDescription()
    private val dummyLat = DataDummy.generateDummyLatitude()
    private val dummyLon = DataDummy.generateDummyLongitude()

    @Before
    fun setUp() {
        storyRepository = StoryRepository(mockDb, mockApi)
    }

    @Test
    fun `when add Story Should Not Null and Return Success`() = runTest {
        val expectedResponse = MutableLiveData<Result<GeneralResponse>>()
        expectedResponse.value = Result.Success(DataDummy.generateDummySuccessGeneralResponse())
        val actualResponse =
            storyRepository.addStory(dummyToken, dummyPhoto, dummyDesc, dummyLat, dummyLon)

        actualResponse.observeForTesting {
            Assert.assertNotNull(actualResponse)
            Assert.assertEquals(
                expectedResponse.value?.data,
                (actualResponse.value as Result.Success).data
            )
        }
    }

    @Test
    fun `when get Stories Should Not Null and Return ListStoryItem `() = runTest {
        val expectedResponse = DataDummy.generateDummySuccessStoriesResponse()
        val actualResponse = mockApi.getStories(dummyToken)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(
            expectedResponse.listStory.size, actualResponse.listStory.size
        )
    }
}