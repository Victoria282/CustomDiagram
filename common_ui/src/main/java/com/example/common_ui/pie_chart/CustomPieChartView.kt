package com.example.common_ui.pie_chart

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.StaticLayout
import android.text.TextPaint
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.common_ui.R
import com.example.common_ui.pie_chart.data.PieData

class CustomPieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val sumText: String by lazy {
        context.getString(
            R.string.pie_chart_view_sum,
            pieData?.totalSum.toString()
        )
    }

    private var pieData: PieData? = null

    private var sweepAngle = MIN_SWEEP_ANGLE

    private var viewWidth = 0F
    private var viewHeight = 0F

    private var arcRectangle: RectF = RectF()
    private var circlePaint: Paint = Paint()
    private var labelPaint: TextPaint = TextPaint()
    private var sumPaint: TextPaint = TextPaint()

    private var labelText: String = ""

    init {
        circlePaint.apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.colorTextInverted)
            isAntiAlias = true
        }

        labelPaint.apply {
            textSize = context.resources.getDimension(R.dimen.font_size_m)
            color = ContextCompat.getColor(context, R.color.colorTextTertiary)
            typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        sumPaint.apply {
            textSize = context.resources.getDimension(R.dimen.font_size_mt)
            color = ContextCompat.getColor(context, R.color.colorTextPrimaryHeadline)
            typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        getAttributes(attrs)
    }

    fun createPieChart(pieData: PieData) {
        this.pieData = pieData
        calculateAngles()
    }

    private fun getAttributes(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.CustomPieChartView, 0, 0).run {
            labelText = getString(R.styleable.CustomPieChartView_pieChartLabel) ?: ""
            recycle()
        }
    }

    private fun calculateAngles() {
        var startAngle = START_ANGLE

        pieData?.slices?.forEach {
            val sweepAngle = it.value.calculateSweepAngle()
            val divider =
                if (sweepAngle == MAX_SWEEP_ANGLE) MIN_SWEEP_ANGLE
                else DIVIDER_DEGREES_VALUE

            it.startAngle = startAngle
            it.sweepAngle = sweepAngle

            startAngle += sweepAngle + divider
            animateArcDrawing(startAngle)
        }
    }

    private fun animateArcDrawing(startAngle: Float) {
        ValueAnimator.ofFloat(sweepAngle, startAngle).apply {
            interpolator = DecelerateInterpolator()
            duration = ANIMATION_DURATION
            addUpdateListener { animator ->
                this@CustomPieChartView.sweepAngle = animator.animatedValue as Float
                this@CustomPieChartView.invalidate()
            }
            start()
        }
    }

    private fun Double.calculateSweepAngle(): Float {
        val isSingleElementList: Boolean = pieData?.slices?.size == SINGLE_ELEMENT_LIST
        val totalSum: Double = pieData?.totalSum ?: EMPTY_SUM

        val sweepAngle =
            if (isSingleElementList) MAX_SWEEP_ANGLE
            else this / totalSum * MAX_SWEEP_ANGLE - DIVIDER_DEGREES_VALUE
        return sweepAngle.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        initMeasurements()
        drawRectangle()
        drawSlices(canvas)
        drawCenterSpace(canvas)
    }

    private fun initMeasurements() {
        viewWidth = width.toFloat()
        viewHeight = height.toFloat()
    }

    private fun drawRectangle() {
        arcRectangle = RectF(0F, 0F, viewWidth, viewHeight)
    }

    private fun drawSlices(canvas: Canvas) {
        pieData?.slices?.forEach {
            val animatedSweepAngle =
                if (sweepAngle < it.startAngle) DEFAULT_ANIMATED_ANGLE
                else minOf(sweepAngle - it.startAngle, it.sweepAngle)

            canvas.drawArc(
                arcRectangle,
                it.startAngle,
                animatedSweepAngle,
                USE_ARC_CENTER_FLAG,
                it.paint
            )
        }
    }

    private fun drawCenterSpace(canvas: Canvas) {
        val indentation = ARC_HEIGHT * 2
        val radius = viewHeight / 2 - indentation
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, radius, circlePaint)
        canvas.drawCenterText()
    }

    private fun Canvas.drawCenterText() {
        val staticLayout = StaticLayout.Builder
            .obtain(labelText, 0, labelText.length, labelPaint, width / 2)
            .build()

        val staticLayoutHeight = staticLayout.height
        val x = viewWidth / 2
        val y = viewHeight / 2 - staticLayoutHeight

        this.drawText(sumText, viewWidth / 2, viewHeight / 2 + staticLayoutHeight / 2, sumPaint)
        this.save()
        this.translate(x, y)
        staticLayout.draw(this)
        this.restore()
    }

    companion object {
        private const val ANIMATION_DURATION = 1 * DateUtils.SECOND_IN_MILLIS
        private const val USE_ARC_CENTER_FLAG = true

        private const val MAX_SWEEP_ANGLE = 360F
        private const val DEFAULT_ANIMATED_ANGLE = 0F
        private const val MIN_SWEEP_ANGLE = 0F
        private const val START_ANGLE = 0F

        private const val DIVIDER_DEGREES_VALUE = 2F
        private const val ARC_HEIGHT = 32F

        private const val SINGLE_ELEMENT_LIST = 1
        private const val EMPTY_SUM = 0.0
    }
}