package com.example.ojectdetection.ui.camera

import android.Manifest
import android.util.Size
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ojectdetection.ui.boundary.BoundingBoxCanvas
import com.example.ojectdetection.viewModel.ObjectDetectionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import dagger.hilt.android.EntryPointAccessors
import java.util.concurrent.ExecutorService

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(viewModel: ObjectDetectionViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera Permission
    val camPerm = rememberPermissionState(permission = Manifest.permission.CAMERA)
    LaunchedEffect(Unit) {
        camPerm.launchPermissionRequest()
    }
    if (!camPerm.status.isGranted) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission required")
        }
        return
    }

    // Get Executor from Hilt
    val executor = remember {
        val ep = EntryPointAccessors.fromApplication(context, CameraScreenEntryPoint::class.java)
        ep.provideExecutor()
    }

    // PreviewView reference
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
    var previewSize by remember { mutableStateOf(Size(0, 0)) }

    // CameraX Controller
    val controller = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
        }
    }

    DisposableEffect(controller) {
        controller.setImageAnalysisAnalyzer(executor) { imageProxy ->
            // Hand over image to ViewModel for ML Kit analysis
            viewModel.onImageProxy(imageProxy)
        }
        onDispose {
            controller.clearImageAnalysisAnalyzer()
            controller.unbind()
        }
    }

    Box(Modifier.fillMaxSize()) {

        // Camera Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    previewViewRef = this
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    this.controller = controller
                    addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
                        previewSize = Size(right - left, bottom - top)
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Bounding Boxes Overlay
        if (previewSize.width > 0 && previewSize.height > 0) {
            BoundingBoxCanvas(
                items = items,
                previewSize = previewSize,
                modifier = Modifier.matchParentSize()
            )
        }

        // Bottom Info Panel
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xAA000000))
                .padding(8.dp)
        ) {
            Text("Detected Objects:", color = Color.White, style = MaterialTheme.typography.titleMedium)
            if (items.isEmpty()) {
                Text("— none —", color = Color.White)
            } else {
                items.forEach {
                    Text("${it.label} ${(it.confidence * 100).toInt()}%  id=${it.trackingId ?: -1}", color = Color.White)
                }
            }
        }
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface CameraScreenEntryPoint {
    fun provideExecutor(): ExecutorService
}
