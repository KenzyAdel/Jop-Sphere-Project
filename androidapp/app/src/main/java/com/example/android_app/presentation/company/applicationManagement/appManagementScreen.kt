package com.example.android_app.presentation.company.applicationManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

// ----------------------
// TOP BAR
// ----------------------
@Composable
fun ApplicationManagementTopBar(
    onBack: () -> Unit = {}
) {
    val topBarColor = Color(0xFF7B1FA2) // Purple consistent with buttons

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
            text = "Application Management",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ----------------------
// SINGLE APPLICANT CARD
// ----------------------
@Composable
fun ApplicantCard(
    applicant: TempApplicantUiItem,
    onViewResume: (String) -> Unit = {},
    onStatusChange: (String, String) -> Unit = { _, _ -> }
) {
    var expanded by remember { mutableStateOf(false) }
    var currentStatus by remember { mutableStateOf(applicant.status) }

    val statusOptions = listOf("Pinned", "Interview", "Accepted", "Rejected", "On Hold")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Applicant details
            Text(applicant.fullName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text("Email: ${applicant.email}", fontSize = 14.sp, color = Color.Gray)
            applicant.phone?.let { Text("Phone: $it", fontSize = 14.sp, color = Color.Gray) }
            applicant.linkedIn?.let { Text("LinkedIn: $it", fontSize = 14.sp, color = Color.Gray) }
            applicant.portfolio?.let { Text("Portfolio: $it", fontSize = 14.sp, color = Color.Gray) }

            Spacer(Modifier.height(8.dp))
            Text("Status: $currentStatus", fontSize = 14.sp, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // View Resume button
                Button(
                    onClick = { onViewResume(applicant.id) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View Resume")
                }

                // Status Dropdown
                Box {
                    Button(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2))
                    ) {
                        Text("Update Status", color = Color.White)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOptions.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    expanded = false
                                    currentStatus = status
                                    onStatusChange(applicant.id, status)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationManagementScreen(
    applicants: List<TempApplicantUiItem> = emptyList(),
    onBack: () -> Unit = {},
    onViewResume: (String) -> Unit = {},
    onStatusChange: (String, String) -> Unit = { _, _ -> }
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ApplicationManagementTopBar(onBack = onBack)

        Spacer(Modifier.height(16.dp))

        if (applicants.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No applicants have applied yet.",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                items(applicants) { applicant ->
                    ApplicantCard(
                        applicant = applicant,
                        onViewResume = onViewResume,
                        onStatusChange = onStatusChange
                    )
                }
            }
        }
    }
}

// Temporary applicant structure for preview
data class TempApplicantUiItem(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val linkedIn: String? = null,
    val portfolio: String? = null,
    val status: String = "Pending"
)

@Preview(showBackground = true)
@Composable
fun ApplicationManagementScreenPreview() {
    val sampleApplicants = listOf(
        TempApplicantUiItem(
            id = "1",
            fullName = "Ahmed Ali",
            email = "ahmed@example.com",
            phone = "01012345678",
            linkedIn = "linkedin.com/in/ahmed",
            portfolio = "ahmedportfolio.com",
            status = "Pending"
        ),
        TempApplicantUiItem(
            id = "2",
            fullName = "Sara Mohamed",
            email = "sara@example.com",
            status = "Interview"
        )
    )

    ApplicationManagementScreen(applicants = sampleApplicants)
}


