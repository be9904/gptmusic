package edu.skku.cs.gptmusic.gpt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.Track
import edu.skku.cs.gptmusic.api.User
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class GPTFragment: Fragment(R.layout.fragment_gpt)  {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gpt, null)

        val generateNum = view.findViewById<EditText>(R.id.generateNum)
        val generateBtn = view.findViewById<Button>(R.id.generateBtn)
        val searchList = view.findViewById<ListView>(R.id.searchList)

        generateBtn.setOnClickListener {
            val intInput = generateNum.text.toString().toIntOrNull()
            if(intInput != null){
                val genList = chooseRandomItems(intInput)
                var songs = ""
                for(track in genList){
                    songs += "${track.name}-${track.artist}, "
                }
                sendChat(songs)
            }
            else if(intInput != null && intInput <= 0)
                Toast.makeText(
                    requireContext(),
                    "Your library is empty!",
                    Toast.LENGTH_SHORT
                ).show()
            else
                Toast.makeText(
                    requireContext(),
                    "Enter valid a number",
                    Toast.LENGTH_SHORT
                ).show()
        }


        return view
    }

    fun chooseRandomItems(n: Int): List<Track> {
        val shuffledList = User.info.savedTracks.shuffled()
        return shuffledList.take(n)
    }


    fun sendChat(songs: String){
        val gptkey = "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"
        val url = "https://api.openai.com/v1/chat/completions"
        val mediaType = "application/json".toMediaType()
        val client = OkHttpClient()

        val prompt =
            "I want you to act as a song recommender. " +
            "I will provide you with a song and you will create a playlist of 30 songs that are " +
            "similar to the given song. Do not choose songs that are same name or artist and do " +
            "not give songs that I give you. Do not write any explanations or other words, just " +
            "reply with the name of song and name of artist. Only answer with in this format: " +
            "\"Song Name-Artist Name, \" without any other words or sentences. I do not want you " +
            "number your answers. My list is \"" +
            "$songs" +
            "\".  This is a sample answer: \"Radioactive - Imagine Dragons, Sugar - Maroon 5, Adore " +
            "You - Harry Styles, Boy With Luv - BTS ft. Halsey, Pompeii - Bastille, Sign of the " +
            "Times - Harry Styles, Viva la Vida - Coldplay, Uptown Funk - Mark , onson ft. Bruno " +
            "Mars, Counting Stars - OneRepublic, Love Yourself - Justin Bieber, " +
            "Don't Stop Believin' - Journey, \"."

        val requestBody = JSONObject()
            .put("messages", listOf(
                JSONObject().put("role", "system").put("content", "I want you to act as a song recommender."),
                JSONObject().put("role", "user").put("content", prompt),
            ))
            .put("model", "text-davinci-001")
            .put("max_tokens", 4000)


        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $gptkey")
            .post(requestBody.toString().toRequestBody(mediaType))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                println(responseBody)
            }
        })
    }
}