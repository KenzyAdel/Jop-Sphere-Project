package com.example.android_app.presentation.authentication.roleSelection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
@Composable
fun RoleSelectionScreen(
    onSelectApplicant: () -> Unit = {},
    onSelectCompany: () -> Unit = {}
) {
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
                text = "Select a Role",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onSelectApplicant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Applicant")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSelectCompany,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Company")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RoleSelectionScreenPreview() {
    MaterialTheme {
        RoleSelectionScreen()
    }
}
