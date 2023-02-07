package com.example.currencyconverterapp

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.airbnb.lottie.LottieAnimationView
import pl.droidsonroids.gif.GifImageView

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val astronautGif = findViewById<GifImageView>(R.id.gifView)
        val textView = findViewById<TextView>(R.id.bestAppTextView)
        textView.alpha = 0f

        astronautGif.animate().setDuration(5000).alpha(1f).withStartAction{
            textView.animate().setDuration(5000).alpha(1f).
            withEndAction{
            }.withEndAction{
                val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(0, android.R.anim.fade_out)
            finish()
            }
        }
    }
}