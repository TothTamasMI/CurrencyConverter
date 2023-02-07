package com.example.currencyconverterapp

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ChartSelectorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_selector)

        val singleButton = findViewById<Button>(R.id.toCalculatorButton)
        val doubleButton = findViewById<Button>(R.id.toAllCalculatorButton)

        singleButton.setOnClickListener {
            val i = Intent(this, ChartActivity::class.java)
            startActivity(i)
        }

        doubleButton.setOnClickListener{
            val i = Intent(this, DoubleChartActivity::class.java)
            startActivity(i)
        }
    }
}