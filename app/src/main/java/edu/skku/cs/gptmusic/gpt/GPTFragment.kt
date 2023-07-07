package edu.skku.cs.gptmusic.gpt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.APIHandler
import edu.skku.cs.gptmusic.api.ChatCompletionResponse
import edu.skku.cs.gptmusic.api.Track
import edu.skku.cs.gptmusic.api.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class GPTFragment: Fragment(R.layout.fragment_gpt)  {
    lateinit var searchList: ListView
    lateinit var progressBar: ProgressBar
    lateinit var timeoutMsg: TextView
    var recList: ArrayList<Track> = ArrayList<Track>(0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gpt, null)

        val generateBtn = view.findViewById<Button>(R.id.generateBtn)
        searchList = view.findViewById(R.id.searchList)
        progressBar = view.findViewById(R.id.progressBarGPT)
        timeoutMsg = view.findViewById(R.id.timeoutMsg)

        generateBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            searchList.visibility = View.VISIBLE
            timeoutMsg.visibility = View.GONE
            recList.clear()
            updateUI()

            if(User.info.savedTracks.isNotEmpty()){
                val genList = chooseRandomItems(7)
                var songs = ""
                for(track in genList){
                    songs += "${track.name}-${track.artist}, "
                }
                sendChat(songs)
            }
            else
                Toast.makeText(
                    requireContext(),
                    "Your library is empty!",
                    Toast.LENGTH_SHORT
                ).show()
        }

        return view
    }

    fun chooseRandomItems(n: Int): List<Track> {
        val shuffledList = User.info.savedTracks.shuffled()
        if(User.info.savedTracks.count() < n)
            return shuffledList.take(User.info.savedTracks.count())
        else
            return shuffledList.take(n)
    }


    fun sendChat(songs: String){
        val gptkey = "GPT API Key"
        val url = "https://api.openai.com/v1/chat/completions"
        val mediaType = "application/json".toMediaType()
        val client = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS) // Read timeout
            .writeTimeout(10, TimeUnit.SECONDS) // Write timeout
            .build()

        val prompt =
            "I want you to act as a song recommender. " +
            "I will provide you with a song and you will create a playlist of 10 songs that are " +
            "similar to the given song. Do not choose songs that are same name or artist and do " +
            "not give songs that I give you. Do not write any explanations or other words, just " +
            "reply with the name of song and name of artist. Only answer with in this format: " +
            "\"Song Name-Artist Name, \" without any other words or sentences. I do not want you " +
            "number your answers. My list is \"" +
            "$songs" +
            "\".  This is a sample answer: \"Radioactive - Imagine Dragons, Sugar - Maroon 5, Adore " +
            "You - Harry Styles, Boy With Luv - BTS ft. Halsey, Pompeii - Bastille, Sign of the " +
            "Times - Harry Styles, \"."

        val messages = JSONArray()
        val systemMessage = JSONObject().put("role", "system").put("content", "I want you to act as a song recommender.")
        val userMessage = JSONObject().put("role", "user").put("content", prompt)
        messages.put(systemMessage)
        messages.put(userMessage)

        val requestBody = JSONObject()
            .put("messages", messages)
            .put("model", "gpt-3.5-turbo")
            .put("max_tokens", 3500)


        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $gptkey")
            .post(requestBody.toString().toRequestBody(mediaType))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if(e is SocketTimeoutException){
                    CoroutineScope(Dispatchers.Main).launch {
                        searchList.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        timeoutMsg.visibility = View.VISIBLE
                    }
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if(responseBody != null){
                    parseRequest(responseBody)
                }
            }
        })
    }

    fun parseRequest(response: String){
        val gson = Gson()
        val response = gson.fromJson(response, ChatCompletionResponse::class.java)

        val pairs = response.choices[0].message.content.split(",").map { it.trim() }
        val parsedSongs = pairs.map { it.split("-").map { it.trim() } }

        // add song to recList
        for (song in parsedSongs) {
            APIHandler.main.getTrack(
                1,
                5,
                song[0]
            )
        }
    }

    fun updateUI(){
        if(recList.isNotEmpty())
            progressBar.visibility = View.GONE

        if(this.isAdded){
            val adapter = GPTAdapter(
                requireContext(),
                parentFragmentManager,
                recList
            )
            searchList.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }
}