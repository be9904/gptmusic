package edu.skku.cs.gptmusic.api

import android.content.Context
import android.widget.ListView
import androidx.fragment.app.FragmentManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import edu.skku.cs.gptmusic.search.SearchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class APIHandler {
    // http request client
    val client = OkHttpClient()
    val host = "https://ws.audioscrobbler.com"

    // firebase
    val database = Firebase.database
    val userDataRef = database.getReference("userdata")

    // code snippet from firebase docs
    // https://firebase.google.com/docs/database/android/read-and-write
    fun fetchUserInfo(email: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for(childSnapshot in dataSnapshot.children) {
                    val user = childSnapshot.getValue(UserData::class.java)
                    // update user info
                    if(user?.email == email) {
                        User.info = user
                        break
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                println("loadPost:onCancelled,  ${databaseError.toException()}")
            }
        }
        userDataRef.addValueEventListener(postListener)
    }

    fun addTrack(){
        
    }

    // request track search to last.fm
    fun trackSearch(
        context: Context,
        supportFragmentManager: FragmentManager,
        listView: ListView,
        pageNumber: Int,
        limit: Int,
        track: String?
    ){
        var trackList = ArrayList<String>(0)

        // set path
        val path = "/2.0/?method=track.search" +
                "&track=$track" +
                "&page=$pageNumber" +
                "&limit=$limit" +
                "&api_key=${User.info.apikey}" +
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
                    val response = gson.fromJson(jsonString, TrackSearchResponse::class.java)

                    val tracks = response.results.trackmatches.track

//                    // Access the track information
//                    for (track in tracks) {
//                        trackList.add("${track.name} - ${track.artist}")
//                        // println("Name: ${track.name} - Artist: ${track.artist}")
//                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        listView.adapter = SearchAdapter(context, supportFragmentManager, tracks)
                    }
                }
            }
        })
    }
}