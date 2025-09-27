package com.example.ojectdetection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ojectdetection.ui.camera.CameraScreen
import com.example.ojectdetection.ui.home.HomeScreen
import com.example.ojectdetection.ui.theme.OjectDetectionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavGraph()
        }
    }
}

@Composable
fun AppNavGraph(){
    val nav = rememberNavController()
    Surface {
        NavHost(navController = nav , startDestination = "home"){
            composable("home") { HomeScreen(onOpenCamera = { nav.navigate("camera") }) }
            composable("camera") { CameraScreen() }
        }
    }
}

