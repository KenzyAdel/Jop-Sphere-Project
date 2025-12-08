package com.example.android_app.presentation.applicant.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class UserProfile(
    val fullName: String,
    val email: String,
    val phone: String,
    val linkedin: String,
    val portfolio: String
)

@Composable
fun UserProfileScreen(
    user: UserProfile,
    onBackClick: () -> Unit = {},
    onSaveClick: (UserProfile) -> Unit = {}
) {
    var isEditing by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf(user.fullName) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phone) }
    var linkedin by remember { mutableStateOf(user.linkedin) }
    var portfolio by remember { mutableStateOf(user.portfolio) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6A1B9A))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val fieldModifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                enabled = isEditing,
                modifier = fieldModifier
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                enabled = isEditing,
                modifier = fieldModifier
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                enabled = isEditing,
                modifier = fieldModifier
            )

            OutlinedTextField(
                value = linkedin,
                onValueChange = { linkedin = it },
                label = { Text("LinkedIn") },
                enabled = isEditing,
                modifier = fieldModifier
            )

            OutlinedTextField(
                value = portfolio,
                onValueChange = { portfolio = it },
                label = { Text("Portfolio") },
                enabled = isEditing,
                modifier = fieldModifier
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isEditing) {
                    Button(
                        onClick = { isEditing = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }
                } else {
                    Button(
                        onClick = {
                            onSaveClick(UserProfile(fullName, email, phone, linkedin, portfolio))
                            isEditing = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }

                    OutlinedButton(
                        onClick = {
                            fullName = user.fullName
                            email = user.email
                            phone = user.phone
                            linkedin = user.linkedin
                            portfolio = user.portfolio
                            isEditing = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Discard")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserProfileScreenPreview() {
    val sampleUser = UserProfile(
        fullName = "Jane Doe",
        email = "jane.doe@example.com",
        phone = "+123456789",
        linkedin = "linkedin.com/in/janedoe",
        portfolio = "janedoe.dev"
    )

    MaterialTheme {
        UserProfileScreen(user = sampleUser)
    }
}