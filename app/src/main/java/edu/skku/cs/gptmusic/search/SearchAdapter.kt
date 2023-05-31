package edu.skku.cs.gptmusic.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import edu.skku.cs.gptmusic.R

class SearchAdapter(val context: Context, val searchedItems: ArrayList<String>): BaseAdapter() {
    override fun getCount(): Int {
        return searchedItems.count()
    }

    override fun getItem(p0: Int): Any {
        return searchedItems[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_search, null)

        val textView = view.findViewById<TextView>(R.id.searchItem)
        textView.text = searchedItems[p0]

        return view
    }
}