package com.example.kotlinjc_camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import kotlinx.serialization.Serializable

@Serializable
object Main

@Composable
fun MainScreen(
    innerPadding: PaddingValues,
    onNavigateToCamera: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Button(onClick = { onNavigateToCamera() }) {
            Text(text = "Take a picture")
        }
    }
}

@PreviewScreenSizes
@Composable
fun MainScreenPreview() {
    val innerPadding = PaddingValues()
    MainScreen(innerPadding, {})
}