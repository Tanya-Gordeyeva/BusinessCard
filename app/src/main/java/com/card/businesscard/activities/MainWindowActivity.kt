package com.card.businesscard.activities


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.card.businesscard.adapters.CardAdapter
import com.card.businesscard.persistense.card.CardDatabaseHandler
import kotlinx.android.synthetic.main.activity_main_window.*
import com.card.businesscard.R
import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Button
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import android.provider.MediaStore
import android.view.View
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import com.card.businesscard.model.Card
import com.card.businesscard.persistense.card.ICardDatabaseHandler
import com.card.businesscard.persistense.email.EmailDatabaseHandler
import com.card.businesscard.persistense.email.IEmailDatabaseHandler
import com.card.businesscard.persistense.phone.IPhoneDatabaseHandler
import com.card.businesscard.persistense.phone.PhoneDatabaseHandler
import com.card.businesscard.persistense.site.ISiteDatabaseHandler
import com.card.businesscard.persistense.site.SiteDatabaseHandler


class MainWindowActivity : AppCompatActivity() {

    private var recyclerView: ListView? = null
    private var dbHelper: ICardDatabaseHandler? = null
    private var dbHelperPhone: IPhoneDatabaseHandler? = null
    private var dbHelperEmail: IEmailDatabaseHandler? = null
    private var dbHelperSite: ISiteDatabaseHandler? = null
    private var Pick_image = 2
    private val Camera_res = 1
    private val ACTION_SAVE = 3
    var dataList: ArrayList<Card>? = null
    lateinit var mAdapter: CardAdapter


    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_window)
        setSupportActionBar(toolbar)

        dbHelper = CardDatabaseHandler(this)
        dbHelperPhone = PhoneDatabaseHandler(this)
        dbHelperEmail = EmailDatabaseHandler(this)
        dbHelperSite = SiteDatabaseHandler(this)
        dataList = ArrayList<Card>()
        getData()

        recyclerView = findViewById<ListView>(R.id.list_view)
        val PickImage = findViewById<Button>(R.id.load_picture) as Button

        val PickCameraImage = findViewById<Button>(R.id.camera) as Button

        PickCameraImage.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, Camera_res)
        }

        PickImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, Pick_image)
        }

        val listView = findViewById<ListView>(R.id.list_view)
        mAdapter = CardAdapter(dataList!!, this)
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
        listView.setClickable(true)

        val searchItem = findViewById<SearchView>(R.id.search)
        searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                mAdapter.getFilter(p0)
                return false
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        when (requestCode) {
            2 -> if (resultCode == Activity.RESULT_OK && imageReturnedIntent?.data != null) {
                val imageStream = contentResolver.openInputStream(imageReturnedIntent.data)
                val selectedImages = BitmapFactory.decodeStream(imageStream) as Bitmap
                val questionIntent = Intent(this@MainWindowActivity,
                        RecognitionActivity::class.java)
                val byteArrayOutputStream = ByteArrayOutputStream()
                selectedImages.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                questionIntent.putExtra("picture", byteArray)
                startActivityForResult(questionIntent, ACTION_SAVE)

            }
            1 -> if (resultCode == Activity.RESULT_OK && imageReturnedIntent != null) {
                val selectedImages = imageReturnedIntent.extras.get("data") as Bitmap
                val questionIntent = Intent(this@MainWindowActivity,
                        RecognitionActivity::class.java)
                val byteArrayOutputStream = ByteArrayOutputStream()
                selectedImages.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                questionIntent.putExtra("picture", byteArray)
                startActivityForResult(questionIntent, ACTION_SAVE)
            }
            3 -> if (resultCode == Activity.RESULT_OK) {
                updateListView()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_window, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> deleteItems()
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getData() {
        val data: List<Card> = dbHelper!!.allCards
        for (i in data) {
            i._phone = dbHelperPhone!!.getPhone(i._id)
            i._email = dbHelperEmail!!.getEmail(i._id)
            i._site = dbHelperSite!!.getSite(i._id)
        }
        dataList!!.addAll(data)
        val textView = findViewById<TextView>(R.id.textView)
        if (dataList!!.size != null) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
        }
    }

    fun updateListView() {
        dataList!!.clear()
        getData()
        mAdapter.updateChecked()
        mAdapter.notifyDataSetChanged()
        mAdapter.updateData()
        val listView = findViewById<ListView>(R.id.list_view)
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
    }

    fun deleteItems(): Boolean {
        val checkedItems: BooleanArray = mAdapter.getChecked()!!
        (0 until checkedItems.size)
                .filter { checkedItems[it] }
                .forEach { dbHelper?.deleteCard(dataList!![it]._id) }
        updateListView()
        return true
    }
}

