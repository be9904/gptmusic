package edu.skku.cs.gptmusic.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.Track
import edu.skku.cs.gptmusic.api.TrackInfoResponse
import edu.skku.cs.gptmusic.api.TrackSearchResponse
import edu.skku.cs.gptmusic.api.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

class TrackInfoFragment(val track: Track): Fragment(R.layout.fragment_trackinfo) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trackinfo, null)

        // get xml elements
        val trackImage = view.findViewById<ImageView>(R.id.trackImage)
        val trackName = view.findViewById<TextView>(R.id.textViewTrack)
        val artistName = view.findViewById<TextView>(R.id.textViewArtist)
        val listeners = view.findViewById<TextView>(R.id.textViewListener)
        val playBtn = view.findViewById<Button>(R.id.playButton)
        val tagsLayout = view.findViewById<LinearLayout>(R.id.tagsLayout)

        // init okhttp
        val client = OkHttpClient()
        val host = "https://ws.audioscrobbler.com"

        // set path
        val path = "/2.0/?method=track.getInfo" +
                "&api_key=${User.info.apikey}" +
                "&artist=${track.artist}" +
                "&track=${track.name}" +
                "&format=json"

        // issue request to last.fm
        val req = Request.Builder()
            .url(host+path)
            .addHeader("Connection", "close")
            .build()

        client.newCall(req).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use{
                    if(!response.isSuccessful)
                        throw IOException("Unexpected code $response")
                    val jsonString = response.body!!.string()

                    val gson = Gson()
                    val response = gson.fromJson(jsonString, TrackInfoResponse::class.java)

                    CoroutineScope(Dispatchers.Main).launch {
                        // set image
                        Glide.with(requireContext())
                            .load(response.track.album.image[3].text)
                            .into(trackImage)

                        // set info
                        trackName.text = response.track.name
                        artistName.text = response.track.artist.name
                        val formattedNumber = NumberFormat
                            .getNumberInstance(Locale.US)
                            .format(response.track.listeners.toInt())
                        listeners.text = "$formattedNumber listeners"

                        // set tags
                        for(tag in response.track.toptags.tag) {
                            val tagLayout = inflater.inflate(R.layout.item_tag, null)
                            tagLayout.findViewById<TextView>(R.id.tag).text = tag.name
                            tagsLayout.addView(tagLayout)
                        }
                    }
                }
            }
        })

        return view
    }

    fun setImage(){

    }
}