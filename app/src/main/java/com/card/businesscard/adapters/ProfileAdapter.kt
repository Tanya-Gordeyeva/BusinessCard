package com.card.businesscard.adapters;

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import com.card.businesscard.R
import com.card.businesscard.model.CardItem
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


class ProfileAdapter(values: ArrayList<CardItem>, lActivity: Activity) : BaseAdapter() {
    private var jsonVal: ArrayList<CardItem> = ArrayList<CardItem>()
    private var inflater: LayoutInflater? = null
    private var activity: Activity? = null

    init {
        activity = lActivity
        inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        jsonVal = values
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
        var vi: View? = p1
        if (p1 == null)
            vi = inflater!!.inflate(R.layout.item_view, null)

        var name: TextView? = vi?.findViewById(R.id.name)
        var desc: EditText? = vi?.findViewById(R.id.desc)

        var item: CardItem = jsonVal[p0]

        name?.text = item.description
        desc?.setText(item.value)
        desc?.setKeyListener(null)

        if (item.orig_description == "phone") {
            desc?.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.value))
                startActivity(this.activity, intent, null)
            }
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
}

