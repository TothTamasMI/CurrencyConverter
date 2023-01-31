package com.example.currencyconverterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CalculatorSelectorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator_selector)

        val calculatorButton = findViewById<Button>(R.id.toCalculatorButton)
        val calculatorToAllButton = findViewById<Button>(R.id.toAllCalculatorButton)

        calculatorButton.setOnClickListener {
            val i = Intent(this, CalculatorActivity::class.java)
            startActivity(i)
        }

        calculatorToAllButton.setOnClickListener{
            val i = Intent(this, CalculateAllActivity::class.java)
            startActivity(i)
        }
    }
}