package com.example.currencyconverterapp

import android.app.Activity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View.inflate
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class CalculatorActivity : AppCompatActivity()  {
    val url = "https://api.exchangerate.host/2020-04-04"
    val symbolsUrl = "https://api.exchangerate.host/symbols"
    var symbolsList = mutableListOf<String>()
    var codeList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        val CalculateButton: Button = findViewById(R.id.calculateButton)
        val ResultTextView: TextView = findViewById(R.id.resultTextView)
        ResultTextView.movementMethod = ScrollingMovementMethod()

        CalculateButton.setOnClickListener {
            runBlocking {
            GlobalScope.async{
                getSymbols()
                }.await()
                var sb = StringBuilder()
                for(i in 0..(symbolsList.size -1)){
                    sb.append(symbolsList.get(i) + " " + codeList.get(i) + "\n")
                }
                runOnUiThread{
                    ResultTextView.text = sb.toString()
                }
            }
        }


    }



    private suspend fun getSymbols() {
        withContext(Dispatchers.Default) {
            val response = URL(symbolsUrl).readText()

            var symbolsText = response.substring(response.indexOf("symbols") + 10)

            while(true){
                if(symbolsText.contains("description")){
                    symbolsList.add(symbolsText.substring(symbolsText.indexOf("description")+14, symbolsText.indexOf("code")-3))
                    codeList.add(symbolsText.substring(symbolsText.indexOf("code") + 7, symbolsText.indexOf("code") + 7 + 3))
                    symbolsText = symbolsText.substring(symbolsText.indexOf("code")+5)
                }
                else{
                    break
                }
            }
        }
    }
}