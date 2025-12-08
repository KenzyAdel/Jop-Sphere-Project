package com.example.android_app.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompanyHomeTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)              // standard app bar height
            .background(Color(0xFF1E88E5))
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Company Dashboard",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
