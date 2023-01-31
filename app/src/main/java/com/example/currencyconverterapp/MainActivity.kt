package com.example.currencyconverterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ChartButon = findViewById<Button>(R.id.ChartButton)
        val CalculatorButton = findViewById<Button>(R.id.CalculatorButton)

        ChartButon.setOnClickListener {
            val i = Intent(this, ChartSelectorActivity::class.java)
            startActivity(i)
        }

        CalculatorButton.setOnClickListener{
            val i = Intent(this,CalculatorSelectorActivity::class.java)
            startActivity(i)
        }

    }
}