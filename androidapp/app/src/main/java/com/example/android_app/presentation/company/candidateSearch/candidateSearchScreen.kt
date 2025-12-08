package com.example.android_app.presentation.company.candidateSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ----------------------
// TOP BAR
// ----------------------
@Composable
fun CandidateSearchTopBar(onBack: () -> Unit = {}) {
    val topBarColor = Color(0xFF7B1FA2)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(topBarColor)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Candidate Search",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ----------------------
// MAIN SCREEN
// ----------------------
@Composable
fun CandidateSearchScreen(
    candidates: List<CandidateUiItem> = emptyList(),
    onViewProfile: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCandidates = candidates.filter {
        it.fullName.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4)),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Top Bar
        item {
            CandidateSearchTopBar()
        }

        // Search bar
        item {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search candidates") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
        }

        // Empty state
        if (filteredCandidates.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No candidates found.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Candidate cards
        items(filteredCandidates) { candidate ->
            CandidateCard(candidate = candidate, onViewProfile = onViewProfile)
        }
    }
}


// ----------------------
// CANDIDATE CARD
// ----------------------
@Composable
fun CandidateCard(
    candidate: CandidateUiItem,
    onViewProfile: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(candidate.fullName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(candidate.email, fontSize = 14.sp, color = Color.Gray)
            candidate.phone?.let {
                Spacer(Modifier.height(2.dp))
                Text("Phone: $it", fontSize = 14.sp, color = Color.Gray)
            }
            candidate.linkedIn?.let {
                Spacer(Modifier.height(2.dp))
                Text("LinkedIn: $it", fontSize = 14.sp, color = Color.Gray)
            }
            candidate.portfolio?.let {
                Spacer(Modifier.height(2.dp))
                Text("Portfolio: $it", fontSize = 14.sp, color = Color.Gray)
            }

            // Jobs applied for
            if (candidate.appliedJobs.isEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text("No jobs applied yet.", fontSize = 14.sp, color = Color.Gray)
            } else {
                Spacer(Modifier.height(6.dp))
                Text("Jobs Applied:", fontWeight = FontWeight.Medium)
                candidate.appliedJobs.forEach { job ->
                    Text("- $job", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(12.dp))

        }
    }
}

// ----------------------
// TEMP STRUCT FOR PREVIEW
// ----------------------
data class CandidateUiItem(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String?,
    val linkedIn: String?,
    val portfolio: String?,
    val appliedJobs: List<String>
)

// ----------------------
// PREVIEW
// ----------------------
@Preview(showBackground = true)
@Composable
fun CandidateSearchScreenPreview() {
    val sampleCandidates = listOf(
        CandidateUiItem(
            id = "1",
            fullName = "Ahmed Ali",
            email = "ahmed.ali@example.com",
            phone = "01012345678",
            linkedIn = "linkedin.com/in/ahmedali",
            portfolio = "ahmedali.dev",
            appliedJobs = listOf("Android Developer", "UI/UX Designer")
        ),
        CandidateUiItem(
            id = "2",
            fullName = "Sara Mahmoud",
            email = "sara.mahmoud@example.com",
            phone = null,
            linkedIn = null,
            portfolio = null,
            appliedJobs = emptyList()
        )
    )
    CandidateSearchScreen(candidates = sampleCandidates)
}
