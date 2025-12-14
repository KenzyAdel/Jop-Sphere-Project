package com.example.android_app.presentation.company.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android_app.data.models.Job

// ----------------------------
// ROUTE COMPOSABLE (Entry Point)
// ----------------------------
@Composable
fun CompanyHomeRoute(
    viewModel: CompanyHomeViewModel = viewModel(),
    onLogout: () -> Unit = {},
    onAddJob: () -> Unit = {},
    onViewApplicants: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadJobs()
    }

    CompanyHomeScreen(
        uiState = uiState,
        onLogout = {
            viewModel.logout()
            onLogout()
        },
        onAddJob = onAddJob,
        onDeleteJob = { jobId -> viewModel.deleteJob(jobId) },
        onViewApplicants = onViewApplicants,
        onRefresh = { viewModel.loadJobs() } // Optional: Pull to refresh logic could use this
    )
}

// ----------------------------
// TOP BAR with Back Button
// ----------------------------
@Composable
fun CompanyHomeTopBar(
    onLogout: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF7B1FD9))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Company Dashboard",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA))
        ) {
            Text("Logout", color = Color.White)
        }
    }
}

// ----------------------------
// MAIN SCREEN
// ----------------------------
@Composable
fun CompanyHomeScreen(
    uiState: CompanyHomeUiState,
    onLogout: () -> Unit = {},
    onDeleteJob: (String) -> Unit = {},
    onViewApplicants: (String) -> Unit = {},
    onAddJob: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {

    Scaffold(
        topBar = { CompanyHomeTopBar(onLogout = onLogout) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F4F4))
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Spacer(Modifier.height(16.dp))

                // "Your Jobs" HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Jobs",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF7B1FD9))
                    }
                } else if (uiState.jobs.isEmpty()) {
                    // EMPTY STATE
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No jobs posted yet.",
                                fontSize = 18.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Create your first job posting to start receiving applications",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    // JOB LIST
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(uiState.jobs) { job ->
                            JobCard(
                                job = job,
                                onDelete = onDeleteJob,
                                onViewApplicants = onViewApplicants
                            )
                        }
                    }
                }
            }

            // Error Message Toast (Simple Text for now if needed, or Snackbar)
            if (uiState.errorMessage != null) {
                 // In a real app, use a SnackbarHost
                 Text(
                     text = uiState.errorMessage,
                     color = Color.Red,
                     modifier = Modifier.align(Alignment.BottomCenter).padding(80.dp)
                 )
            }

            // ADD BUTTON - Square plus button in bottom right corner
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Card(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF7B1FD9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    IconButton(
                        onClick = onAddJob,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add new job",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------
// SINGLE JOB CARD
// ----------------------------
@Composable
fun JobCard(
    job: Job,
    onDelete: (String) -> Unit,
    onViewApplicants: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Top row: Job title + Delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = job.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(text = job.location, fontSize = 14.sp, color = Color.Gray)
                    if (job.salary.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(text = "Salary: ${job.salary}", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Button(
                    onClick = { onDelete(job.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Delete", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            // View Applicants button (always visible)
            Button(
                onClick = { onViewApplicants(job.id) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Applicants", fontSize = 14.sp)
            }
        }
    }
}

// ----------------------------
// PREVIEW
// ----------------------------
@Preview(showBackground = true, widthDp = 360)
@Composable
fun CompanyHomeScreenPreview() {
    val sampleJobs = listOf(
        Job(id = "1", title = "Android Developer", location = "Cairo", salary = "20k - 30k EGP"),
        Job(id = "2", title = "UI/UX Designer", location = "Alexandria", salary = ""),
        Job(id = "3", title = "Backend Engineer", location = "Remote", salary = "25k - 40k EGP")
    )
    CompanyHomeScreen(
        uiState = CompanyHomeUiState(jobs = sampleJobs),
        onAddJob = { /* Handle add job click */ }
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun CompanyHomeScreenEmptyPreview() {
    CompanyHomeScreen(
        uiState = CompanyHomeUiState(jobs = emptyList()),
        onAddJob = { /* Handle add job click */ }
    )
}
