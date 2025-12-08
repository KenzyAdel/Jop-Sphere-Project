package com.example.android_app.presentation.company.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CompanySignUpScreen(
    onSignUpClick: (
        companyName: String,
        email: String,
        password: String,
        website: String?,
        phone: String?,
        industry: String?,
        linkedIn: String?
    ) -> Unit = { _, _, _, _, _, _, _ -> },
    onNavigateToLogin: () -> Unit = {}
) {
    // Required fields
    var companyName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Optional fields
    var website by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var industry by remember { mutableStateOf("") }
    var linkedIn by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEDED)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Company Sign Up",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Company Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Optional fields
            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Website (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone (Optional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = industry,
                onValueChange = { industry = it },
                label = { Text("Industry (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = linkedIn,
                onValueChange = { linkedIn = it },
                label = { Text("LinkedIn (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSignUpClick(
                        companyName,
                        email,
                        password,
                        website.ifBlank { null },
                        phone.ifBlank { null },
                        industry.ifBlank { null },
                        linkedIn.ifBlank { null }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Login")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CompanySignUpScreenPreview() {
    MaterialTheme {
        CompanySignUpScreen()
    }
}
