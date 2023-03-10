package com.example.common_ui.pie_chart

import android.content.Context
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.example.common_ui.R
import com.example.common_ui.pie_chart.data.PieData
import com.example.common_ui.pie_chart.data.PieSlice

class PieBuilder(private val context: Context) {

    private val pieSlices = mutableListOf<PieSlice>()
    private var totalSum = 0.0

    fun add(value: Double, colorId: Int): PieBuilder {
        pieSlices.add(
            PieSlice(
                value = value,
                startAngle = DEFAULT_ANGLE,
                sweepAngle = DEFAULT_ANGLE,
                paint = createPaint(colorId)
            )
        )
        totalSum += value
        return this
    }

    fun build(): PieData {
        val isEmpty = pieSlices.isEmpty()
        if (isEmpty) pieSlices.add(defaultPieSlice())

        return PieData(
            slices = pieSlices,
            totalSum = totalSum
        )
    }

    private fun defaultPieSlice() = PieSlice(
        value = EMPTY_SUM,
        startAngle = DEFAULT_ANGLE,
        sweepAngle = MAX_SWEEP_ANGLE,
        paint = createPaint(R.color.colorTextPrimary)
    )

    private fun createPaint(colorId: Int): Paint = Paint().apply {
        this.color = ContextCompat.getColor(context, colorId)
        this.isAntiAlias = true
        this.style = Paint.Style.FILL
    }

    companion object {
        private const val EMPTY_SUM = 0.0
        private const val DEFAULT_ANGLE = 0F
        private const val MAX_SWEEP_ANGLE = 360F
    }
}