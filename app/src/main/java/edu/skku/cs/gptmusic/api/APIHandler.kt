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
import edu.skku.cs.gptmusic.HomeActivity
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
    var userRef = database.getReference("userdata")
    var userIndex = -1

    // code snippet(modified) from firebase docs
    // https://firebase.google.com/docs/database/android/read-and-write
    fun initialFetch(email: String) {
        // get list of user data
        userDataRef.get().addOnSuccessListener {
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
            val userListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
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
            userRef = userDataRef.child(userIndex.toString())
            userRef.addValueEventListener(userListener)

            // fetch current user data for the first time
            userRef.get().addOnSuccessListener {
                val data = it.getValue(UserData::class.java)
                if(data != null)
                    User.info = data
                HomeActivity.fragment1.updateUI()
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    // write to firebase (add tracks)
    fun addTrack(context: Context, newTrack: Track){
        // handler for toast
        val handler = Handler(Looper.getMainLooper())

        // add selected track
        val trackList = ArrayList<Track>(User.info.savedTracks)
        for(track in trackList){
            if(track.name == newTrack.name
                && track.artist == newTrack.artist) {
                handler.post{
                    Toast.makeText(
                        context,
                        "Track already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
        trackList.add(newTrack)
        updateImageURL(trackList)

        // show toast message
        handler.post {
            Toast.makeText(
                context,
                "Added Track to List",
                Toast.LENGTH_SHORT
            ).show()
        }
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

    // get album image url
    fun updateImageURL(trackList: ArrayList<Track>){
        val newTrack = trackList.last()
        val trackImages = newTrack.image.toMutableList()

        // set path
        val path = "/2.0/?method=track.getInfo" +
                "&api_key=${User.info.apikey}" +
                "&artist=${newTrack.artist}" +
                "&track=${newTrack.name}" +
                "&format=json"

        // issue request to last.fm
        val req = Request.Builder()
            .url(host+path)
            .addHeader("Connection", "close")
            .build()

        client.newCall(req).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if(!response.isSuccessful)
                    throw IOException("Unexpected code $response")

                val jsonString = response.body!!.string()

                val gson = Gson()
                val response = gson.fromJson(jsonString, TrackInfoResponse::class.java)
                val imageUrl = response?.track?.album?.image?.get(
                    response?.track?.album?.image?.count()!!-1
                )?.text ?: ""
                println(imageUrl)

                for(i in trackImages.indices){
                    trackImages[i] = Image(imageUrl, "extralarge")
                }

                trackList[trackList.count()-1] =
                    Track(
                        newTrack.name,
                        newTrack.artist,
                        newTrack.url,
                        newTrack.streamable,
                        newTrack.listeners,
                        trackImages,
                        newTrack.mbid
                    )

                // write to firebase
                userRef.child("savedTracks").setValue(trackList)
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