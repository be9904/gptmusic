package edu.skku.cs.gptmusic.settings

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import edu.skku.cs.gptmusic.R

class SettingsAdapter(
    val context: Context,
    val settingsFields: ArrayList<SettingsField>
):BaseAdapter() {
    override fun getCount(): Int {
        return settingsFields.count()
    }

    override fun getItem(p0: Int): Any {
        return settingsFields[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.settings_field, null)

        val fieldText = view.findViewById<TextView>(R.id.textViewField)
        fieldText.text = settingsFields[p0].fieldText

        if(p0 == settingsFields.count() - 1) {
            fieldText.setTextColor(Color.RED)
            fieldText.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        }

        return view
    }
}