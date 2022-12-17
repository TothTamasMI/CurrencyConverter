package com.example.currencyconverterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import kotlinx.coroutines.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ChartActivity : AppCompatActivity() {
    private var fromSpinner: SmartMaterialSpinner<String>? = null
    private var toSpinner: SmartMaterialSpinner<String>? = null

    private var date: String ?= null
    private val symbolsUrl = "https://api.exchangerate.host/symbols"
    private var symbolsList = mutableListOf<String>()
    private var codeList = mutableListOf<String>()
    private var fromCurrency: String ?= null
    private var toCurrency: String ?= null
    private var fromCurrencyCode: String ?= null
    private var toCurrencyCode: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        runBlocking {
            GlobalScope.async{
                getSymbols()
            }.await()
            initSpinners()
        }

        val weekButton = findViewById<Button>(R.id.weekButton)
        val monthButton = findViewById<Button>(R.id.monthButton)
        val yearButton = findViewById<Button>(R.id.yearButton)
        val resultTextView = findViewById<TextView>(R.id.chartResultTextView)

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
        date = currentDate

        weekButton.setOnClickListener {
            val c = Calendar.getInstance()
            c.add(Calendar.DATE, -6)
            val startDate = sdf.format(c.getTime())
            resultTextView.text = startDate
        }

        monthButton.setOnClickListener {
            val c = Calendar.getInstance()
            c.add(Calendar.DATE, -29)
            val startDate = sdf.format(c.getTime())
            resultTextView.text = startDate
        }

        yearButton.setOnClickListener {
            val c = Calendar.getInstance()
            c.add(Calendar.DATE, -364)
            val startDate = sdf.format(c.getTime())
            resultTextView.text = startDate
        }
    }

    private fun initSpinners() {
        fromSpinner = findViewById(R.id.fromSpinner)
        toSpinner = findViewById(R.id.toSpinner)

        fromSpinner?.item = symbolsList
        toSpinner?.item = symbolsList

        fromSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                fromCurrency = symbolsList!![position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        toSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                toCurrency = symbolsList!![position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
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
            for (i in 0..symbolsList.size - 1){
                symbolsList[i] = symbolsList[i] + " (" + codeList[i] + ")"
            }
        }
    }
}