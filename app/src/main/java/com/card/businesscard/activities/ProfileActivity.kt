package com.card.businesscard.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.ListView
import com.card.businesscard.R
import com.card.businesscard.model.CardItem
import com.card.businesscard.adapters.ProfileAdapter
import com.card.businesscard.model.Card
import com.card.businesscard.persistense.card.CardDatabaseHandler
import com.card.businesscard.persistense.email.EmailDatabaseHandler
import com.card.businesscard.persistense.phone.PhoneDatabaseHandler
import com.card.businesscard.persistense.site.SiteDatabaseHandler

import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.io.FileInputStream

class ProfileActivity : AppCompatActivity() {
    var card: Card? = null

    private var dbHelper: CardDatabaseHandler? = null
    private var dbHelperPhone: PhoneDatabaseHandler? = null
    private var dbHelperEmail: EmailDatabaseHandler? = null
    private var dbHelperSite: SiteDatabaseHandler? = null
    lateinit var mAdapter: ProfileAdapter

    var dataList: ArrayList<CardItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)

        dbHelper = CardDatabaseHandler(this)
        dbHelperPhone = PhoneDatabaseHandler(this)
        dbHelperEmail = EmailDatabaseHandler(this)
        dbHelperSite = SiteDatabaseHandler(this)

        var cardId = intent.getStringExtra("cardId")

        dataList = ArrayList<CardItem>()
        card = getData(cardId!!)
        generateItems(card!!)

        val listView = findViewById<ListView>(R.id.recycler_view_json)

        mAdapter = ProfileAdapter(dataList!!, this)
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

        val bitmap = try {
            BitmapFactory.decodeStream(FileInputStream(File(card!!._image)))
        } catch (e: Exception) {
            null
        }
        val picture = findViewById<ImageView>(R.id.imageView)
        picture.setImageBitmap(bitmap)

    }

    fun getData(id: String): Card {
        val data: Card = dbHelper!!.getCard(id.toInt())
        data._phone = dbHelperPhone!!.getPhone(data._id)
        data._email = dbHelperEmail!!.getEmail(data._id)
        data._site = dbHelperSite!!.getSite(data._id)
        return data
    }

    fun generateItems(card: Card) {
        if (card._surname != null) {
            dataList!!.add(CardItem("surname", "Фамилия", card._surname))
        }
        if (card._name != null) {
            dataList!!.add(CardItem("name", "Имя", card._name))
        }
        if (card._fathername != null) {
            dataList!!.add(CardItem("fathername", "Отчество", card._fathername))
        }
        if (card._phone != null) {
            dataList!!.add(CardItem("phone", "Телефон", card._phone!![0].toString()))
            if (card._phone!!.size > 1) {
                for (i in 1 until card._phone.size)
                    dataList!!.add(CardItem("phone", "", card._phone!![0].toString()))
            }
        }
        if (card._email != null) {
            dataList!!.add(CardItem("email", "Почта", card._email[0].toString()))
            if (card._email.size > 1) {
                for (i in 1 until card._email.size)
                    dataList!!.add(CardItem("email", "", card._email[i].toString()))
            }
        }
        if (card._site != null) {
            dataList!!.add(CardItem("site", "Сайт", card._site[0].toString()))
            if (card._site.size > 1) {
                for (i in 1 until card._site.size)
                    dataList!!.add(CardItem("site", "", card._site[i].toString()))
            }
        }
        if (card._address != null) {
            dataList?.add(CardItem("address", "Адрес", card._address.toString()))
        }
        if (card._notes != null) {
            dataList?.add(CardItem("notes", "Дополнительно", card._notes.toString()))
        }
    }

}
