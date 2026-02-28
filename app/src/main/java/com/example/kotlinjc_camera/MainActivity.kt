package com.example.kotlinjc_camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
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

            KotlinJCCameraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navigationController,
                        startDestination = Main
                    ) {
                        composable<Main> { MainScreen(
                            innerPadding,
                            { navigationController.navigate(Camera) }
                       ) }
                        composable<Camera> { CameraScreen({navigationController.popBackStack()}) }
                    }
                }
            }
        }
    }
}