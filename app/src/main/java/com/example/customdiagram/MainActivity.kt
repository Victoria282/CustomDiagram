package com.example.customdiagram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.common_ui.pie_chart.PieBuilder
import com.example.customdiagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpPieChart()
    }

    private fun setUpPieChart() = with(binding) {
        val data = PieBuilder(this).apply {
            add(65.0, R.color.expenses_cameras)
            add(105.0, R.color.expenses_internet)
            add(440.0, R.color.expenses_tv)
            add(80.0, R.color.expenses_smart_intercom)
            add(150.0, R.color.expenses_smart_intercom_pro)
        }.build()

        customPieChartView.createPieChart(data)
    }
}