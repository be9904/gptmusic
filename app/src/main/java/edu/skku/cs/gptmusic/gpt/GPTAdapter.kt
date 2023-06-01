package edu.skku.cs.gptmusic.gpt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.Track
import edu.skku.cs.gptmusic.search.TrackInfoFragment
import java.text.NumberFormat
import java.util.Locale

class GPTAdapter(
     val context: Context,
     val supportFragmentManager: FragmentManager,
     val generatedSongs: List<Track>
): BaseAdapter() {
    override fun getCount(): Int {
        return generatedSongs.count()
    }

    override fun getItem(p0: Int): Any {
        return generatedSongs[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_search, null)

        val textView = view.findViewById<TextView>(R.id.searchItem)
        val textViewListener = view.findViewById<TextView>(R.id.textViewListeners)

        textView.text = "${generatedSongs[p0].name} - ${generatedSongs[p0].artist}"
        val formattedNumber = NumberFormat
            .getNumberInstance(Locale.US)
            .format(generatedSongs[p0].listeners.toInt())
        textViewListener.text = "$formattedNumber listeners"

        view.setOnClickListener{
            val fragment = TrackInfoFragment(generatedSongs[p0]) // Replace `AnotherFragment` with the desired fragment class
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            // Set custom animations for enter and exit transitions
            transaction.setCustomAnimations(
                R.anim.enter_from_right, // Enter animation for TrackInfoFragment
                R.anim.exit_to_left, // Exit animation for SearchFragment
                R.anim.enter_from_left, // Enter animation for SearchFragment (when coming back)
                R.anim.exit_to_right // Exit animation for TrackInfoFragment (when coming back)
            )

            transaction.replace(R.id.flFragment, fragment)
            transaction.addToBackStack(null) // Add the transaction to the back stack
            transaction.commit()
        }

        return view
    }
}