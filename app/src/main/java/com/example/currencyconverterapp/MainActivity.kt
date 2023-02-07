package com.example.currencyconverterapp

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chartButon = findViewById<Button>(R.id.ChartButton)
        val calculatorButton = findViewById<Button>(R.id.CalculatorButton)

        chartButon.setOnClickListener {
            val i = Intent(this, ChartSelectorActivity::class.java)
            startActivity(i)
        }

        calculatorButton.setOnClickListener{
            val i = Intent(this,CalculatorSelectorActivity::class.java)
            startActivity(i)
        }

    }
}