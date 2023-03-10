package com.example.common_ui.pie_chart.data

import android.graphics.Paint

data class PieSlice(
    val value: Double,
    val paint: Paint,
    var startAngle: Float,
    var sweepAngle: Float,
)