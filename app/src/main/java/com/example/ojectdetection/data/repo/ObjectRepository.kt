package com.example.ojectdetection.data.repo

import androidx.camera.core.ImageProxy
import com.example.ojectdetection.data_class.DetectedItem
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetector
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class ObjectRepository @Inject constructor(
    private val detector: ObjectDetector
) {
    /**
     * Detect objects using ML Kit. Always closes imageProxy in onComplete.
     * Returns list of DetectedItem (may be empty).
     */

    suspend fun detect(imageProxy: ImageProxy): List<DetectedItem> =
        suspendCancellableCoroutine { cont ->
            val mediaImage = imageProxy.image
            if(mediaImage == null){
                imageProxy.close()
                cont.resume(emptyList()){ }
                return@suspendCancellableCoroutine
            }

            val rotation = imageProxy.imageInfo.rotationDegrees
            val inputImage = InputImage.fromMediaImage(mediaImage,rotation)

            detector.process(inputImage)
                .addOnSuccessListener { objects ->
                    val mapped = objects.map { o->
                        val label = o.labels.firstOrNull()?.text ?: "Unknown"
                        val confidence = o.labels.firstOrNull()?.confidence ?: 0f
                        val box = o.boundingBox ?: android.graphics.Rect(0,0,0,0)
                        DetectedItem(label, confidence, o.trackingId, box)
                    }
                    cont.resume(mapped) { }
                }
                .addOnFailureListener{
                    cont.resume(emptyList()){ }
                }
                .addOnCompleteListener{
                    imageProxy.close()
                }
        }
}