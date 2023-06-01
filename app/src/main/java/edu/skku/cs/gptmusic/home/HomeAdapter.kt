package edu.skku.cs.gptmusic.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import edu.skku.cs.gptmusic.HomeActivity
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.APIHandler
import edu.skku.cs.gptmusic.api.Track
import edu.skku.cs.gptmusic.search.TrackInfoFragment
import java.text.NumberFormat
import java.util.Locale

class HomeAdapter(
    val context: Context,
    val supportFragmentManager: FragmentManager,
    val trackList: List<Track>
): BaseAdapter() {
    override fun getCount(): Int {
        return trackList.count()
    }

    override fun getItem(p0: Int): Any {
        return trackList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_track, null)

        // set track info to card
        val albumImage = view.findViewById<ImageView>(R.id.albumImage)
        val trackTitle = view.findViewById<TextView>(R.id.trackTitle)
        val artistName = view.findViewById<TextView>(R.id.artistName)
        val listenerCnt = view.findViewById<TextView>(R.id.listenerCnt)
        val deleteImage = view.findViewById<ImageView>(R.id.deleteImage)

        // set image
        if(trackList?.get(p0)?.image?.get(
                trackList?.get(p0)?.image?.count()!! - 1
            )?.text != null){
            if(trackList?.get(p0)?.image?.get(
                    trackList?.get(p0)?.image?.count()!! - 1
                )?.text != ""){
                Glide.with(context)
                    .load(trackList[p0]
                        .image[trackList[p0].image.count() - 1].text)
                    .into(albumImage)
            }
        }

        // set track info
        trackTitle.text = trackList?.get(p0)?.name ?: "-"
        artistName.text = trackList?.get(p0)?.artist ?: "-"
        if(trackList?.get(p0)?.listeners != null){
            val formattedNumber = NumberFormat
                .getNumberInstance(Locale.US)
                .format(trackList[p0].listeners.toInt())
            listenerCnt.text = "$formattedNumber listeners"
        }
        else
            listenerCnt.text = ""

        deleteImage.setOnClickListener{
            APIHandler.main.removeTrack(context, trackList[p0])
        }

        view.setOnClickListener{
            val fragment = TrackInfoFragment(trackList[p0]) // Replace `AnotherFragment` with the desired fragment class
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