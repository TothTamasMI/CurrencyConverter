package com.example.currencyconverterapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class CalculateAllActivity : AppCompatActivity()  {
    //--------------------GLOBAL VARIABLES---------------------\\
    private var fromSpinner: SmartMaterialSpinner<String>? = null
    private var toSpinner: SmartMaterialSpinner<String>? = null

    private var resultTextView: TextView ?= null
    private var amountEditText: EditText ?= null

    private var fromCurrency: String ?= null
    private var toCurrency: String ?= null
    private var fromCurrencyCode: String ?= null
    private var toCurrencyCode: String ?= null
    private var amount = 1.0
    private var date: String ?= null

    private val symbolsUrl = "https://api.exchangerate.host/symbols"
    private var symbolsList = mutableListOf<String>()
    private var codeList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        try{

            //---------------INTIT AND FILL SPINNERS---------------\\
            runBlocking {
                GlobalScope.async{
                    getSymbols()
                }.await()
                initSpinners()
            }

            //--------------------INIT CALENDAR--------------------\\
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = sdf.format(Date())

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerButton = findViewById<Button>(R.id.datePickerButton)
            datePickerButton.text = currentDate
            date = currentDate

            datePickerButton.setOnClickListener {
                val datePickerDialog = DatePickerDialog(
                    this, DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->
                        val mMonthCopy = mMonth + 1
                        var mMonthString = mMonthCopy.toString()
                        if (mMonthCopy < 10){
                            mMonthString = "" + 0 + mMonthString
                        }

                        datePickerButton.text = "" + mYear +"-" + mMonthString + "-" + mDay
                        date = "" + mYear +"-" + mMonthString + "-" + mDay
                    }, year, month, day)
                datePickerDialog.show()
            }

            val CalculateButton: Button = findViewById(R.id.calculateButton)
            resultTextView = findViewById(R.id.resultTextView)
            val resultTextView = findViewById<TextView>(R.id.resultTextView)

            amountEditText = findViewById<EditText>(R.id.amountEditText)

            resultTextView!!.movementMethod = ScrollingMovementMethod()


            CalculateButton.setOnClickListener {
                if (amountEditText!!.text.toString() != ""){
                    amount = amountEditText!!.text.toString().toDouble()
                }
                else{
                    amount = 1.0
                }
                calculateCurrency()
            }}catch(e : Exception){
            internetPopUp()
        }

    }

    private fun internetPopUp(){
        MaterialAlertDialogBuilder(this)
            .setTitle("Internet error!").setMessage("No internet, please try again!")
            .setPositiveButton("Refresh"){dialog, which ->
                finish();
                startActivity(getIntent());
            }
            .show()
    }

    private fun showSnackbar(message: String){
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun calculateCurrency() {
        //https://api.exchangerate.host/2020-04-04?base=USD
        try{
            if(fromCurrency!!.endsWith(")") && toCurrency!!.endsWith(")")){
                fromCurrencyCode = fromCurrency!!.substring(fromCurrency!!.length-4, fromCurrency!!.length-1)
                toCurrencyCode = toCurrency!!.substring(toCurrency!!.length-4, toCurrency!!.length-1)
                var URL = "https://api.exchangerate.host/"+ date +"?base=" + fromCurrencyCode

                runBlocking {
                    GlobalScope.async{
                        getRateFromURL(URL)
                    }.await()}
                //var result = (rate!! * amount).toString()

                //resultTextView!!.text = "" + amount + " " + fromCurrencyCode + " = " +  result + " " + toCurrencyCode
            }
            else{
                println("API ERROR Wrong currency codes")
            }
        }
        catch (e : Exception){
            resultTextView!!.text = "ERROR, Please try again or try another currency or date"
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

    private suspend fun getRateFromURL(URL : String){
        withContext(Dispatchers.Default) {
            val response = URL(URL).readText()
            println(URL)
            resultTextView!!.text =response
        }
    }

}