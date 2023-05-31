package edu.skku.cs.gptmusic.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.Track
import java.text.NumberFormat
import java.util.Locale

class SearchAdapter(val context: Context, val searchedItems: List<Track>): BaseAdapter() {
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
        val textViewListener = view.findViewById<TextView>(R.id.textViewListeners)

        textView.text = "${searchedItems[p0].name} - ${searchedItems[p0].artist}"
        val formattedNumber = NumberFormat
            .getNumberInstance(Locale.US)
            .format(searchedItems[p0].listeners.toInt())
        textViewListener.text = "$formattedNumber listeners"

        return view
    }
}