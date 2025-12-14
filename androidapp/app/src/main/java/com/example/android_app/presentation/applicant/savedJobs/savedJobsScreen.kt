package com.example.android_app.presentation.applicant.savedJobs
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_app.data.entities.SavedJobsEntity

@Composable
fun SavedJobsScreen(
    uiState: savedJobsUiState,
    onBackClick: () -> Unit,
    onApplyClick: (SavedJobsEntity) -> Unit,
    onRemoveClick: (SavedJobsEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6A1B9A))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Saved Jobs",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        if (uiState.savedJobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "You have no saved jobs yet.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.savedJobs) { job ->
                SavedJobCard(
                    job = job,
                    onApplyClick = { onApplyClick(job) },
                    onRemoveClick = { onRemoveClick(job) }
                )
            }
        }
    }
}

@Composable
fun SavedJobCard(
    job: SavedJobsEntity,
    onApplyClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(job.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))

            Text(job.company, fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))

            Text("Location: ${job.location}", fontSize = 15.sp)
            Text("Salary: ${job.salary}", fontSize = 15.sp)
            Text("Type: ${job.jobType}", fontSize = 15.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onRemoveClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Remove")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onApplyClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SavedJobsScreenPreview() {
    val sampleJobs = listOf(
        SavedJobsEntity(
            id = "job1",
            title = "Android Developer",
            company = "Tech Corp",
            location = "Remote",
            salary = "$60,000",
            jobType = "Full Time"
        ),
        SavedJobsEntity(
            id = "job2",
            title = "Junior Kotlin Developer",
            company = "Soft Solutions",
            location = "New York, USA",
            salary = "$45,000",
            jobType = "Intern"
        )
    )

    SavedJobsScreen(
        uiState = savedJobsUiState(isLoading = false, savedJobs = sampleJobs),
        onBackClick = {},
        onApplyClick = {},
        onRemoveClick = {}
    )
}