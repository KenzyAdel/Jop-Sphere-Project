package com.example.android_app

import com.example.android_app.data.entities.SavedJobsEntity
import com.example.android_app.data.repositories.SavedJobRepository
import com.example.android_app.data.services.ApplicantService
import com.example.android_app.data.services.ApplicationService
import com.example.android_app.data.services.CompanyService
import com.example.android_app.data.services.JobService
import com.example.android_app.presentation.applicant.home.ApplicantHomeViewModel
import com.example.android_app.presentation.applicant.home.JobItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ApplicantHomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock lateinit var savedJobRepository: SavedJobRepository
    @Mock lateinit var jobService: JobService
    @Mock lateinit var companyService: CompanyService
    @Mock lateinit var applicationService: ApplicationService
    @Mock lateinit var applicantService: ApplicantService

    @Mock lateinit var mockAuth: FirebaseAuth
    @Mock lateinit var mockUser: FirebaseUser

    private lateinit var viewModel: ApplicantHomeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        `when`(mockAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn("test_applicant_123")

        runTest {
            `when`(jobService.getAllJobs()).thenReturn(Result.success(emptyList()))
            `when`(companyService.getAllCompanies()).thenReturn(Result.success(emptyList()))
        }
    }

    @Test
    fun `onSaveJob calls repository saveJob when job is NOT currently saved`() = runTest {
        `when`(savedJobRepository.getSavedJobs()).thenReturn(flowOf(emptyList()))

        viewModel = ApplicantHomeViewModel(
            jobService,
            companyService,
            applicationService,
            applicantService,
            savedJobRepository,
            mockAuth
        )

        advanceUntilIdle()

        val jobToSave = JobItem(
            id = "firebase_job_123",
            title = "Android Dev",
            company = "Google",
            location = "Remote",
            salary = "100k",
            description = "Desc",
            responsibilities = "Resp",
            requirements = "Req"
        )

        viewModel.onSaveJob(jobToSave)
        advanceUntilIdle()

        verify(savedJobRepository).saveJob("firebase_job_123")
    }

    @Test
    fun `onSaveJob calls repository removeJob when job IS already saved`() = runTest {
        val alreadySavedEntity = SavedJobsEntity(
            id = "firebase_job_123",
            title = "Dev",
            company = "Google",
            location = "US",
            salary = "100k",
            jobType = "Full"
        )

        `when`(savedJobRepository.getSavedJobs()).thenReturn(flowOf(listOf(alreadySavedEntity)))

        viewModel = ApplicantHomeViewModel(
            jobService,
            companyService,
            applicationService,
            applicantService,
            savedJobRepository,
            mockAuth
        )

        advanceUntilIdle()

        val jobToUnsave = JobItem(
            id = "firebase_job_123",
            title = "Android Dev",
            company = "Google",
            location = "Remote",
            salary = "100k",
            description = "Desc",
            responsibilities = "Resp",
            requirements = "Req"
        )

        viewModel.onSaveJob(jobToUnsave)
        advanceUntilIdle()

        verify(savedJobRepository).removeJob(alreadySavedEntity)
    }
}
