package edu.skku.cs.gptmusic.api

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ListView
import android.widget.Toast
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
    companion object{
        val main = APIHandler() // super lazy singleton initialization
    }

    // http request client
    val client = OkHttpClient()
    val host = "https://ws.audioscrobbler.com"

    // firebase
    val database = Firebase.database
    val userDataRef = database.getReference("userdata")
    var userIndex = -1
    var rawJson = ""

    // code snippet(modified) from firebase docs
    // https://firebase.google.com/docs/database/android/read-and-write
    fun initialFetch(email: String) {
        // get list of user data
        userDataRef.get().addOnSuccessListener {
            println("got userdata: $it")

            // get user info from datasnapshot list
            var index = 0
            for(childSnapshot in it.children) {
                val user = childSnapshot.getValue(UserData::class.java)
                // update user info
                if(user?.email == email) {
                    User.info = user
                    userIndex = index
                    break
                }
                index++
            }

            // create listener for corresponding user index
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    println("fetched user $userIndex data update")
                    val user = dataSnapshot.getValue(UserData::class.java)
                    // update user info
                    if (user != null) {
                        User.info = user
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    println("loadPost:onCancelled,  ${databaseError.toException()}")
                }
            }

            // add listener
            userDataRef.child(userIndex.toString()).addValueEventListener(postListener)
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    // write to firebase (add tracks)
    fun addTrack(track: Track){
        // modify track
        val trackList = ArrayList<Track>(User.info.savedTracks)
        trackList.add(track)

        // write to firebase
    }

    // check if api key is usable
    fun checkAPIKey(context: Context, apikey: String){
        // set path
        val path = "/2.0/?method=track.search" +
                "&track=Believe" +
                "&api_key=$apikey" +
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
                    if(response.code == 403){
                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            Toast.makeText(
                                context,
                                "Please Set a Valid API Key in Settings",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
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

                    CoroutineScope(Dispatchers.Main).launch {
                        listView.adapter = SearchAdapter(context, supportFragmentManager, tracks)
                    }
                }
            }
        })
    }
}