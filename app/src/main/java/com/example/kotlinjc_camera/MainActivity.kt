package com.example.kotlinjc_camera

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlinjc_camera.ui.theme.KotlinJCCameraTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navigationController = rememberNavController()
            var currentPhoto by remember { mutableStateOf<Bitmap?>(null) }

            KotlinJCCameraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navigationController,
                        startDestination = Main
                    ) {
                        composable<Main> {
                            MainScreen( innerPadding ) { navigationController.navigate(Camera) }
                        }
                        composable<Camera> {
                            CameraScreen(
                                {currentPhoto = it},
                                { navigationController.navigate(ImageS) }
                            )
                        }
                        composable<ImageS> {
                            ImageScreen(
                                innerPadding,
                                currentPhoto
                            ) { navigationController.popBackStack() }
                        }
                    }
                }
            }
        }
    }
}