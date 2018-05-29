package com.card.businesscard.adapters;

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.card.businesscard.model.CardItem
import android.view.LayoutInflater
import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.card.businesscard.R


class ListAdapter(values: ArrayList<CardItem>, lActivity: Activity) : BaseAdapter() {
    private var jsonVal: ArrayList<CardItem> = ArrayList<CardItem>()
    private var inflater: LayoutInflater? = null
    private var activity: Activity? = null

    init{
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
        desc?.addTextChangedListener(object : TextWatcher  {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p01: CharSequence?, p1: Int, p2: Int, p3: Int) {
                jsonVal[p0].value= desc.text.toString()
            }

            override fun afterTextChanged(s: Editable ) {
                jsonVal[p0].value= desc.text.toString()
            }
        })
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
