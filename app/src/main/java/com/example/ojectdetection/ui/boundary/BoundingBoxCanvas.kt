package com.example.ojectdetection.ui.boundary

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.example.ojectdetection.data_class.DetectedItem
import kotlin.math.min

@Composable
fun BoundingBoxCanvas(
    items: List<DetectedItem>,
    previewSize: android.util.Size,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val pw = size.width
        val ph = size.height

        // find approximate image bounds from items (fallback)
        val proxyMaxWidth = items.maxOfOrNull { it.boundingBox.right }?.toFloat() ?: 0f
        val proxyMaxHeight = items.maxOfOrNull { it.boundingBox.bottom }?.toFloat() ?: 0f

        // If no boxes, nothing to draw
        if (proxyMaxWidth <= 0f || proxyMaxHeight <= 0f) {
            return@Canvas
        }

        // scale and center-crop mapping
        val scale = min(pw / proxyMaxWidth, ph / proxyMaxHeight)
        val scaledImgW = proxyMaxWidth * scale
        val scaledImgH = proxyMaxHeight * scale
        val dx = (pw - scaledImgW) / 2f
        val dy = (ph - scaledImgH) / 2f

        items.forEach { it ->
            val box = it.boundingBox
            val left = box.left * scale + dx
            val top = box.top * scale + dy
            val right = box.right * scale + dx
            val bottom = box.bottom * scale + dy

            // Draw bounding rectangle
            drawRect(
                color = Color.Green,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 4f)
            )

            // Draw label background (fixing size type)
            drawRect(
                color = Color(0xAA000000), // ✅ Direct Int color
                topLeft = Offset(left, top - 30f),
                size = Size(
                    (it.label.length * 14f).coerceAtLeast(60f), // ✅ Ensure Float
                    30f
                )
            )

            // Draw label text
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${it.label} ${(it.confidence * 100).toInt()}%",
                    left + 8f,
                    top - 6f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 28f
                        isFakeBoldText = true
                    }
                )
            }
        }
    }
}
