package com.example.currencyconverterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Thread.sleep(3000)
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}