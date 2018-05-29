package com.card.businesscard.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.card.businesscard.R
import com.card.businesscard.model.Card
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat.startActivity
import android.widget.*
import com.card.businesscard.activities.ProfileActivity
import java.io.File
import java.io.FileInputStream
import android.R.attr.data


class CardAdapter(values: ArrayList<Card>, lActivity: Activity) : BaseAdapter() {
    private var jsonVal: ArrayList<Card> = ArrayList<Card>()
    private var origData: ArrayList<Card>
    private var inflater: LayoutInflater? = null
    private var activity: Activity? = null
    private var checked: BooleanArray?

    init {
        activity = lActivity
        inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        jsonVal = values
        checked = BooleanArray(values.size)
        origData = ArrayList<Card>(jsonVal)
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
        var vi: View? = p1
        if (p1 == null)
            vi = inflater!!.inflate(R.layout.raw_layout, null)

        var name: TextView? = vi?.findViewById(R.id.name)
        var surname: TextView? = vi?.findViewById(R.id.surname)
        var fathername: TextView? = vi?.findViewById(R.id.fathername)
        var image: ImageView? = vi?.findViewById(R.id.image)
        var check: CheckBox? = vi?.findViewById(R.id.checkbox)
        var bitmap: Bitmap?
        val layout: LinearLayout? = vi?.findViewById(R.id.layout_view)

        var item: Card = jsonVal[p0]
        bitmap = try {
            BitmapFactory.decodeStream(FileInputStream(File(item._image)))
        } catch (e: Exception) {
            null
        }
        if (checked?.isNotEmpty()!!) {
            check?.setChecked(checked!![p0])
        }
        check!!.setOnClickListener {
            checked!![p0] = !checked!![p0]
        }
        surname?.text = item._surname
        name?.text = item._name
        fathername?.text = item._fathername
        image?.setImageBitmap(bitmap)
        layout?.setOnClickListener {
            val intentCard = Intent(this.activity,
                    ProfileActivity::class.java)
            intentCard.putExtra("cardId", jsonVal[p0]._id.toString())
            startActivity(this.activity, intentCard, null)
        }
        return vi
    }

    override fun getItem(p0: Int): Any {
        return jsonVal[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount() = jsonVal.size

    fun getChecked() = checked

    fun updateChecked() {
        checked = BooleanArray(jsonVal.size)
    }

    fun updateData() {
        origData.clear()
        origData.addAll(jsonVal)
    }

    fun getFilter(text: String?) {
        jsonVal.clear()
        if (text.isNullOrEmpty()) {
            jsonVal.addAll(origData)
        } else {
            jsonVal.addAll(origData.filter { it._fathername.contains(text as CharSequence, true) || it._name.contains(text as CharSequence, true) || it._surname.contains(text as CharSequence, true) })
        }
        notifyDataSetChanged()
    }
}

