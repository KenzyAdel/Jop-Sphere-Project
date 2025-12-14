package com.example.android_app

import com.example.android_app.data.entities.SavedJobsEntity
import com.example.android_app.data.repositories.SavedJobRepository
import com.example.android_app.presentation.applicant.savedJobs.savedJobsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class savedJobsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var repository: SavedJobRepository

    private lateinit var viewModel: savedJobsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `loadSavedJobs updates uiState with jobs when repository returns data`() = runTest {
        val fakeJobsList = listOf(
            SavedJobsEntity("1", "Dev", "Google", "Remote", "100k", "FullTime"),
            SavedJobsEntity("2", "QA", "Amazon", "Cairo", "80k", "PartTime")
        )

        `when`(repository.getSavedJobs()).thenReturn(flowOf(fakeJobsList))

        viewModel = savedJobsViewModel(repository)

        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(fakeJobsList, viewModel.uiState.value.savedJobs)
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `removeJob calls repository delete method`() = runTest {
        // Arrange
        val jobToRemove = SavedJobsEntity("1", "Dev", "Google", "Remote", "100k", "FullTime")
        `when`(repository.getSavedJobs()).thenReturn(flowOf(emptyList()))

        viewModel = savedJobsViewModel(repository)
        advanceUntilIdle()

        viewModel.removeJob(jobToRemove)
        advanceUntilIdle()

        verify(repository).removeJob(jobToRemove)
    }
}
