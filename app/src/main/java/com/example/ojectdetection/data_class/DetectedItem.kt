package com.example.ojectdetection.data_class

import android.graphics.Rect

data class DetectedItem(
    val label : String,
    val confidence : Float,
    val trackingId : Int?,
    val boundingBox : Rect
)
