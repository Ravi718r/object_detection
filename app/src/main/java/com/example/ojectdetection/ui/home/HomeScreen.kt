package com.example.ojectdetection.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ojectdetection.R

@Composable
fun HomeScreen(onOpenCamera: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fixed the Icon painter issue
        IconButton(onClick = onOpenCamera) {
            Icon(
                painter = painterResource(id = R.drawable.camer_alt), // ✅ FIXED
                contentDescription = "Open Camera"
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Tap camera to start")
    }
}
