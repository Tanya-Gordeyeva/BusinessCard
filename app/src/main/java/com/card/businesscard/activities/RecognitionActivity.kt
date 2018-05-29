package com.card.businesscard.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.card.businesscard.R
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.os.Environment
import android.provider.MediaStore
import android.text.format.Time
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import com.card.businesscard.model.CardItem
import com.card.businesscard.adapters.ListAdapter
import com.card.businesscard.model.Card
import com.card.businesscard.persistense.card.CardDatabaseHandler
import com.card.businesscard.persistense.email.EmailDatabaseHandler
import com.card.businesscard.persistense.phone.PhoneDatabaseHandler
import com.card.businesscard.persistense.site.SiteDatabaseHandler
import com.squareup.okhttp.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.concurrent.TimeUnit


class RecognitionActivity : AppCompatActivity() {
    private var decodedImage: Bitmap? = null
    private var base_url: String = "http://10.0.2.2:8000/api/"
    private var client = OkHttpClient()
    var dataList: ArrayList<CardItem>? = null
    lateinit var mAdapter: ListAdapter
    private var dbHelper: CardDatabaseHandler? = null
    private var dbHelperPhone: PhoneDatabaseHandler? = null
    private var dbHelperEmail: EmailDatabaseHandler? = null
    private var dbHelperSite: SiteDatabaseHandler? = null

    @SuppressLint("WrongViewCast", "PrivateResource")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognition)

        val picture = intent.getByteArrayExtra("picture")
        decodedImage = BitmapFactory.decodeByteArray(picture, 0, picture.size) as Bitmap
        val cardImage = findViewById<ImageView>(R.id.picture_view)
        cardImage.setImageBitmap(decodedImage)

        dbHelper = CardDatabaseHandler(this)
        dbHelperPhone = PhoneDatabaseHandler(this)
        dbHelperEmail = EmailDatabaseHandler(this)
        dbHelperSite = SiteDatabaseHandler(this)

        val startRecognition = findViewById<Button>(R.id.rec) as Button


        val byteArrayOutputStream = ByteArrayOutputStream()
        val selectedImages = decodedImage?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        startRecognition.setOnClickListener {
            startRecognitionOnClick(byteArray)
            startRecognition.visibility = View.GONE
        }
        client.setConnectTimeout(50, TimeUnit.SECONDS)
        client.setReadTimeout(50, TimeUnit.SECONDS)
        val color: Int = resources.getColor(R.color.material_blue_grey_950)
        findViewById<ProgressBar>(R.id.progress).indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        findViewById<ProgressBar>(R.id.progress).visibility = View.GONE

        val continueRecognition = findViewById<Button>(R.id.button_continue2) as Button
        continueRecognition.setOnClickListener {
            val textForClassification = findViewById<TextView>(R.id.text).text
            classificateText(textForClassification)
        }

        val listView = findViewById<ListView>(R.id.recycler_view_json)
        dataList = ArrayList<CardItem>()
        mAdapter = ListAdapter(dataList!!, this)
        listView.adapter = mAdapter

        var totalH = 0
        val viewG = listView
        for (temp in 0 until mAdapter.count) {
            val listItem = mAdapter.getView(temp, null, viewG)
            listItem!!.measure(0, 0)
            totalH += listItem.measuredHeight
        }
        val pars = listView.layoutParams
        pars.height = totalH + (listView.dividerHeight * (mAdapter.count - 1))
        listView.layoutParams = pars
        listView.requestLayout()

        val saveButton = findViewById<Button>(R.id.save) as Button
        saveButton.setOnClickListener {
            saveCardAction()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    fun startRecognitionOnClick(byteArray: ByteArray) {
        val requestBody = RequestBody.create(MediaType.parse("text/html"), byteArray)
        val request = Request.Builder()
                .url(base_url + "bounds")
                .post(requestBody)
                .build()
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            @SuppressLint("WrongViewCast")
            override fun onResponse(response: Response?) {
                if (!response?.isSuccessful!!) {
                    throw IOException("Unexpected code " + response)
                }
                val text = response.body().string()
                this@RecognitionActivity.runOnUiThread({
                    val drawIntent = Intent(this@RecognitionActivity,
                            CanvasActivity::class.java)
                    drawIntent.putExtra("image", byteArray)
                    drawIntent.putExtra("bounds", text)
                    startActivityForResult(drawIntent, 1)
                })
            }

            override fun onFailure(request: Request?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        when (requestCode) {
            1 -> if (resultCode == Activity.RESULT_OK && imageReturnedIntent != null) {
                val boundsString = imageReturnedIntent.extras.get("newBounds").toString()
                continueRecognitionText(boundsString)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    fun continueRecognitionText(boundsString: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val selectedImages = decodedImage?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        var json: JSONObject = JSONObject()
        json.put("bounds", boundsString)
        json.put("image", Base64.encodeToString(byteArray, Base64.DEFAULT))
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val request = Request.Builder()
                .url(base_url + "textRecognition")
                .post(requestBody)
                .build()
        findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                e?.printStackTrace()
            }

            @SuppressLint("WrongViewCast")
            override fun onResponse(response: Response?) {
                if (!response?.isSuccessful!!) {
                    throw IOException("Unexpected code " + response)
                }
                val text = response.body().string()
                this@RecognitionActivity.runOnUiThread({
                    findViewById<TextView>(R.id.text).text = text
                    findViewById<TextView>(R.id.text).visibility = View.VISIBLE
                    findViewById<Button>(R.id.button_continue2).visibility = View.VISIBLE
                    findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
                })
            }
        })
    }

    private fun classificateText(textForClassification: CharSequence) {
        val requestBody = RequestBody.create(MediaType.parse("text/plain"), textForClassification.toString())
        val request = Request.Builder()
                .url(base_url + "classification")
                .post(requestBody)
                .build()
        findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE
        findViewById<TextView>(R.id.text).visibility = View.GONE
        findViewById<Button>(R.id.button_continue2).visibility = View.GONE
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                e?.printStackTrace()
            }

            @SuppressLint("WrongViewCast")
            override fun onResponse(response: Response?) {
                if (!response?.isSuccessful!!) {
                    throw IOException("Unexpected code " + response)
                }
                val text = JSONObject(response.body().string())
                this@RecognitionActivity.runOnUiThread({
                    findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
                    findViewById<TextView>(R.id.text).text = text.toString()
                    findViewById<Button>(R.id.save).visibility = View.VISIBLE
                    val listView = findViewById<ListView>(R.id.recycler_view_json)
                    listView.visibility = View.VISIBLE
                    dataList!!.clear()
                    generateItems(text)
                    mAdapter.notifyDataSetChanged()
                    var totalH = 0
                    val viewG = listView
                    for (temp in 0 until mAdapter.count) {
                        val listItem = mAdapter.getView(temp, null, viewG)
                        listItem!!.measure(0, 0)
                        totalH += listItem.measuredHeight
                    }
                    val pars = listView.layoutParams
                    pars.height = totalH + (listView.dividerHeight * (mAdapter.count - 1))
                    listView.layoutParams = pars
                    listView.requestLayout()
                })
            }

        })
    }

    fun generateItems(text: JSONObject) {
        dataList!!.add(CardItem("surname", "Фамилия", text.get("surname").toString()))
        dataList!!.add(CardItem("name", "Имя", text.get("name").toString()))
        dataList!!.add(CardItem("fathername", "Отчество", text.get("fathername").toString()))
        if (text.has("phone")) {
            val phone = text.get("phone") as JSONArray?
            if (phone!!.length() > 0) {
                dataList!!.add(CardItem("phone", "Телефон", phone!!.get(0).toString()))
                if (phone!!.length() > 1) {
                    for (i in 1 until phone.length())
                        dataList!!.add(CardItem("phone", "", phone.get(i).toString()))
                }
            }
        }
        if (text.has("email")) {
            val email = text.get("email") as JSONArray?
            if (email!!.length() > 0) {
                dataList!!.add(CardItem("email", "Почта", email?.get(0).toString()))
                if (email!!.length() > 1) {
                    for (i in 1 until email.length())
                        dataList!!.add(CardItem("email", "", email.get(i).toString()))
                }
            }
        }
        if (text.has("site")) {
            val site = text.get("site") as JSONArray?
            if (site!!.length() > 0) {
                dataList!!.add(CardItem("site", "Сайт", site.get(0).toString()))
                if (site.length() > 1) {
                    for (i in 1 until site.length())
                        dataList!!.add(CardItem("site", "", site.get(i).toString()))
                }
            }
        }
        if (text.has("address")) {
            dataList?.add(CardItem("address", "Адрес", text.get("address").toString()))
        }
        if (text.has("notes")) {
            dataList?.add(CardItem("notes", "Дополнительно", text.get("notes").toString()))
        }
    }

    fun saveCardAction() {
        val listView = findViewById<ListView>(R.id.recycler_view_json)
        var card: Card = Card()
        for (i in 0 until listView.adapter.count) {
            var item = listView.adapter.getItem(i) as CardItem
            if (item.orig_description == "surname") card._surname = item.value.toUpperCase()
            if (item.orig_description == "name") card._name = item.value.toUpperCase()
            if (item.orig_description == "fathername") card._fathername = item.value.toUpperCase()
            if (item.orig_description == "address") card._address = item.value
            if (item.orig_description == "notes") card._notes = item.value
        }
        val img: String = savePicture()
        card._image = img
        val id: Int = dbHelper!!.addCard(card).toInt()
        for (i in 0 until listView.adapter.count) {
            val item = listView.adapter.getItem(i) as CardItem
            if (item.orig_description == "phone") {
                dbHelperPhone!!.addPhone(item.value, id)
            }
            if (item.orig_description == "site") {
                dbHelperSite!!.addSite(item.value, id)
            }
            if (item.orig_description == "email") {
                dbHelperEmail!!.addEmail(item.value, id)
            }
        }
    }

    fun savePicture(): String {
        var fOut: OutputStream? = null
        val time: Time = Time()
        time.setToNow()
        val imgName: String = Integer.toString(time.year) + Integer.toString(time.month) + Integer.toString(time.monthDay) + Integer.toString(time.hour) + Integer.toString(time.minute) + Integer.toString(time.second) + ".png"
        try {
            val file: File = getImgStorageDir(this, "businesscard", imgName)
            fOut = FileOutputStream(file)

            val bitmap: Bitmap = decodedImage as Bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
            MediaStore.Images.Media.insertImage(contentResolver, file.absolutePath, file.name, file.name)
        } catch (e: Exception) {
            return e.localizedMessage
        }
        return this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).path + "/" + imgName
    }

    fun getImgStorageDir(context: Context, dir: String, filename: String): File {
        val file = File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), filename)
        if (!context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES).mkdirs()) {
            Log.i("Info", "Directory not created")
        }
        return file
    }
}

