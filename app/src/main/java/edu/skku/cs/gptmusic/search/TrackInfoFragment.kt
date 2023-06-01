package edu.skku.cs.gptmusic.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import edu.skku.cs.gptmusic.HomeActivity
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.APIHandler
import edu.skku.cs.gptmusic.api.Track
import edu.skku.cs.gptmusic.api.TrackInfoResponse
import edu.skku.cs.gptmusic.api.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
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
        val desc = view.findViewById<TextView>(R.id.textViewDesc)
        val playBtn = view.findViewById<Button>(R.id.playButton)
        val addBtn = view.findViewById<Button>(R.id.addButton)
        val tagsLayout = view.findViewById<LinearLayout>(R.id.tagsLayout)

        // get youtube link
        CoroutineScope(Dispatchers.Default).launch {
            // Fetch the html content of the Last.fm track page
            val doc: Document = Jsoup.connect(track.url).get()

            // Extract the link from the html
            val pattern = "data-youtube-url=\"(.*?)\"".toRegex()
            val matchResult = pattern.find(doc.toString())
            val youtubeLink = matchResult?.groupValues?.get(1)
            playBtn.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
                val chooser = Intent.createChooser(intent, "Open with")
                startActivity(chooser)
            }
        }

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
                    if(response.code == 403)
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "Please Set a Valid API Key in Settings",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    else if(!response.isSuccessful)
                        throw IOException("Unexpected code $response")

                    val jsonString = response.body!!.string()

                    val gson = Gson()
                    val response = gson.fromJson(jsonString, TrackInfoResponse::class.java)

                    CoroutineScope(Dispatchers.Main).launch {
                        // set image
                        if(response.track?.album?.image?.get(3)?.text != null){
                            Glide.with(requireContext())
                                .load(response.track.album.image[3].text)
                                .into(trackImage)
                        }

                        // set info
                        trackName.text = response.track?.name ?: "-"
                        artistName.text = response.track?.artist?.name ?: "-"
                        if(response.track?.listeners != null){
                            val formattedNumber = NumberFormat
                                .getNumberInstance(Locale.US)
                                .format(response.track?.listeners.toInt())
                            listeners.text = "$formattedNumber listeners"
                        }
                        else
                            listeners.text = ""
                        val summary = response.track?.wiki?.summary
                        if(summary != null){
                            // Parse the html and create a spanned object
                            val parsedHtml = HtmlCompat.fromHtml(summary, HtmlCompat.FROM_HTML_MODE_LEGACY)

                            // Create a SpannableStringBuilder to combine the parsed HTML with non-hyperlink text
                            val builder = SpannableStringBuilder()
                            builder.append("\n\n")
                            builder.append(parsedHtml)

                            // Set the combined text in the TextView
                            desc.text = builder

                            // Enable clickable links in the TextView
                            desc.movementMethod = LinkMovementMethod.getInstance()
                        }
                        else
                            desc.text = "No Summary Available"

                        // set tags
                        if(response.track?.toptags?.tag != null){
                            for(tag in response.track?.toptags?.tag) {
                                val tagLayout = inflater.inflate(R.layout.item_tag, null)
                                tagLayout.findViewById<TextView>(R.id.tag).text = tag.name
                                tagsLayout.addView(tagLayout)
                            }
                        }

                        // set add button
                        addBtn.setOnClickListener {
                            // add data to firebase
                            APIHandler.main.addTrack(requireContext(), track)
                        }
                    }
                }
            }
        })

        return view
    }
}