package edu.skku.cs.gptmusic.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.Tag

class TagAdapter(
    val context: Context,
    val tags: List<Tag>
): BaseAdapter() {
    override fun getCount(): Int {
        return tags.count()
    }

    override fun getItem(p0: Int): Any {
        return tags[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_tag, null)

        val tag = view.findViewById<TextView>(R.id.tag)
        tag.text = tags[p0].name

        return view
    }
}