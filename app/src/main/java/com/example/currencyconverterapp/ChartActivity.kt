package com.example.currencyconverterapp

import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class ChartActivity : AppCompatActivity() {
    //----------------------------GLOBAL VARIABLES---------------------------\\
    private var fromSpinner: SmartMaterialSpinner<String>? = null
    private var toSpinner: SmartMaterialSpinner<String>? = null

    private var fromCurrency: String ?= null
    private var toCurrency: String ?= null
    private var fromCurrencyCode: String ?= null
    private var toCurrencyCode: String ?= null

    private var date: String ?= null
    private var min: Double ?= null
    private var max: Double ?= null
    private var minDate: String ?= null
    private var maxDate: String ?= null
    private var streak: Int ?= null
    private var actual: Double ?= null

    private val symbolsUrl = "https://api.exchangerate.host/symbols"
    private var symbolsList = mutableListOf<String>()
    private var timeList = mutableListOf<String>()
    private var dataList = mutableListOf<Double>()
    private var codeList = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        try{

        //------------------------INTIT AND FILL SPINNERS--------------------\\
            runBlocking {
                GlobalScope.async{
                    getSymbols()
                }.await()
                initSpinners()
            }

        //------------------------INIT AND DEFINE VARIABLES------------------\\
            val imageButton = findViewById<ImageButton>(R.id.resultChart)
            val weekButton = findViewById<Button>(R.id.weekButton)
            val monthButton = findViewById<Button>(R.id.monthButton)
            val yearButton = findViewById<Button>(R.id.yearButton)
            val resultTextView = findViewById<TextView>(R.id.chartResultTextView)
            var image: Bitmap? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDate = sdf.format(Date())
            date = currentDate

            //------------------------MAKE WEEK BUTTON-----------------------\\
            weekButton.setOnClickListener {
                try{
                    resultTextView.text = "Loading..."
                    if(fromCurrency!!.endsWith(")") && toCurrency!!.endsWith(")")){
                        fromCurrencyCode = fromCurrency!!.substring(fromCurrency!!.length-4, fromCurrency!!.length-1)
                        toCurrencyCode = toCurrency!!.substring(toCurrency!!.length-4, toCurrency!!.length-1)

                        val c = Calendar.getInstance()
                        c.add(Calendar.DATE, -6)
                        val startDate = sdf.format(c.getTime())
                        val timeSeriesURL = "https://api.exchangerate.host/timeseries?start_date=" + startDate + "&end_date=" + currentDate + "&base=" + fromCurrencyCode + "&symbols=" + toCurrencyCode

                        runBlocking {
                            GlobalScope.async{
                                getRateFromURL(timeSeriesURL)
                            }.await()}

                        //--------LOAD IMAGE---------------------------------\\
                        val executor = Executors.newSingleThreadExecutor()
                        val handler = Handler(Looper.getMainLooper())
                        executor.execute {
                            try {
                                val `in` = java.net.URL(makeChartURL()).openStream()
                                image = BitmapFactory.decodeStream(`in`)

                                handler.post {
                                    imageButton.setImageBitmap(image)
                                }

                                setMinMaxStreak()
                                runOnUiThread{
                                    resultTextView.text = "\nminimum value = " + min + " (" + minDate + ")" + "\nmaximum value = " + max + " (" + maxDate + ")"+  "\nstreak = " + streak + " days" + "\nactual = " + actual + "\n"
                                }
                            }
                            catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                catch (e : Exception){
                    resultTextView!!.text = "ERROR, Please try again or try another currency or date"
                }
            }

            //------------------------MAKE MONTH BUTTON----------------------\\
            monthButton.setOnClickListener {
                try{
                    resultTextView.text = "Loading..."
                    if(fromCurrency!!.endsWith(")") && toCurrency!!.endsWith(")")){
                        fromCurrencyCode = fromCurrency!!.substring(fromCurrency!!.length-4, fromCurrency!!.length-1)
                        toCurrencyCode = toCurrency!!.substring(toCurrency!!.length-4, toCurrency!!.length-1)

                        val c = Calendar.getInstance()
                        c.add(Calendar.DATE, -29)
                        val startDate = sdf.format(c.getTime())
                        val timeSeriesURL = "https://api.exchangerate.host/timeseries?start_date=" + startDate + "&end_date=" + currentDate + "&base=" + fromCurrencyCode + "&symbols=" + toCurrencyCode

                        runBlocking {
                            GlobalScope.async{
                                getRateFromURL(timeSeriesURL)
                            }.await()}

                        //--------LOAD IMAGE---------------------------------\\
                        val executor = Executors.newSingleThreadExecutor()
                        val handler = Handler(Looper.getMainLooper())
                        executor.execute {
                            try {
                                val `in` = java.net.URL(makeChartURL()).openStream()
                                image = BitmapFactory.decodeStream(`in`)

                                handler.post {
                                    imageButton.setImageBitmap(image)
                                }

                                setMinMaxStreak()
                                runOnUiThread{
                                    resultTextView.text = "\nminimum value = " + min + " (" + minDate + ")" + "\nmaximum value = " + max + " (" + maxDate + ")"+  "\nstreak = " + streak + " days" + "\nactual = " + actual + "\n"
                                }
                            }
                            catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                catch (e : Exception){
                    resultTextView!!.text = "ERROR, Please try again or try another currency or date"
                }
            }

            //------------------------MAKE YEAR BUTTON-----------------------\\
            yearButton.setOnClickListener {
                resultTextView.text = "Loading..."
                try{
                    if(fromCurrency!!.endsWith(")") && toCurrency!!.endsWith(")")){
                        fromCurrencyCode = fromCurrency!!.substring(fromCurrency!!.length-4, fromCurrency!!.length-1)
                        toCurrencyCode = toCurrency!!.substring(toCurrency!!.length-4, toCurrency!!.length-1)

                        val c = Calendar.getInstance()
                        c.add(Calendar.DATE, -364)
                        val startDate = sdf.format(c.getTime())
                        val timeSeriesURL = "https://api.exchangerate.host/timeseries?start_date=" + startDate + "&end_date=" + currentDate + "&base=" + fromCurrencyCode + "&symbols=" + toCurrencyCode

                        runBlocking {
                            GlobalScope.async{
                                getRateFromURL(timeSeriesURL)
                            }.await()}

                        //--------LOAD IMAGE---------------------------------\\
                        val executor = Executors.newSingleThreadExecutor()
                        val handler = Handler(Looper.getMainLooper())
                        executor.execute {
                            try {
                                val `in` = java.net.URL(makeChartURL()).openStream()
                                image = BitmapFactory.decodeStream(`in`)

                                handler.post {
                                    imageButton.setImageBitmap(image)
                                }
                            }
                            catch (e: Exception) {
                                e.printStackTrace()
                            }

                            setMinMaxStreak()
                            runOnUiThread{
                                resultTextView.text = "\nminimum value = " + min + " (" + minDate + ")" + "\nmaximum value = " + max + " (" + maxDate + ")"+  "\nstreak = " + streak + " days" + "\nactual = " + actual + "\n"
                            }
                        }
                    }
                }
                catch (e : Exception){
                    resultTextView!!.text = "ERROR, Please try again or try another currency or date"
                }
            }

            //------------------------MAKE IMAGE DOWNLOADABLE----------------\\
            imageButton.setOnClickListener {
                if (image != null) {
                    mSaveMediaToStorage(image)
                }
            }
        }
        catch(e : Exception){
            internetPopUp()
        }
    }

    //----------------------------MAKE "NO INTERNET" POP UP------------------\\
    private fun internetPopUp(){
        MaterialAlertDialogBuilder(this)
            .setTitle("Internet error!").setMessage("No internet, please try again!")
            .setPositiveButton("Refresh"){dialog, which ->
                finish();
                startActivity(getIntent());
            }
            .show()
    }

    //----------------------------SET THE MIN, MAX, STREAK VALUES------------\\
    private fun setMinMaxStreak(){
        min = dataList.get(0)
        max = dataList.get(0)
        minDate = ""
        maxDate = ""
        streak = 0
        for (i in 0..(dataList.size-1)){
            if(dataList.get(i) > max!!){
                max = dataList.get(i)
                maxDate = timeList.get(i)
            }
            if(dataList.get(i) < min!!){
                min = dataList.get(i)
                minDate = timeList.get(i)
            }
            if(i > 0){
                if(dataList.get(i) >= dataList.get(i - 1)){
                    streak = streak!!+1
                }
                else{
                    streak = 0
                }
            }
        }
        actual = dataList.get(dataList.size-1)
    }

    //----------------------------INIT THE SPINNERS--------------------------\\
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

    //----------------------------GET THE CURRENCY CODES FROM THE API--------\\
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

    //----------------------------GET THE RATE FROM THE API------------------\\
    private suspend fun getRateFromURL(URL : String){
        withContext(Dispatchers.Default) {
            //println(URL)
            val response = URL(URL).readText()
            var datas = response.substring(response.indexOf("\"rates\":{") + 10)
            timeList.clear()
            dataList.clear()
            for (data in datas.split("},\"")){
                val timeAndData = data.replace("}}}", "")
                timeList.add(timeAndData.substring(0, 10))
                dataList.add((timeAndData.substring(19)).toDouble())
                //println(timeAndData.substring(0, 10))
                //println(timeAndData.substring(19))
            }
        }
    }

    //----------------------------MAKE THE URL FOR THE CHART IMAGE-----------\\
    private fun makeChartURL():String{
        var chartURL = "https://quickchart.io//chart?c={ type: 'line', data: { labels: ["
        if(timeList.size < 200){
            for (time in timeList){
                chartURL += "'" + time + "' ,"
            }
        }
        else{
            var counter = 0
            for (time in timeList){
                if (counter % 2 == 0){
                    chartURL += "'" + time + "' ,"
                }
                counter += 1
            }
        }
        chartURL = chartURL.substring(0, chartURL.length-2)
        chartURL += "], datasets: [ { label:"
        chartURL += "'" + toCurrency + "'"
        chartURL += ", backgroundColor: 'rgb(255, 99, 132)', borderColor: 'rgb(255, 99, 132)', data: ["
        if (dataList.size < 200) {
            for (data in dataList) {
                chartURL += "'" + data + "' ,"
            }
        }
        else{
            var counter = 0
            for (date in dataList){
                if (counter % 2 == 0){
                    chartURL += "'" + date + "' ,"
                }
                counter += 1
            }
        }
        chartURL = chartURL.substring(0, chartURL.length-2)
        chartURL += "], fill: false, } ], }, options: { title: { display: true, text:"
        chartURL += "'Data for the last " + dataList.size + " days'"
        chartURL += ", },scales: {yAxes: [{ticks: {beginAtZero: false,},},],},},}"
        return chartURL
    }

    //----------------------------DOWNLOAD THE IMAGE-------------------------\\
    //----------------------------COPIED FROM THE INTERNET-------------------\\
    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        //-------------------THE WHOLE FUNCTION COPYED FROM THE INTERNET------------------\\
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }}
