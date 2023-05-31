package edu.skku.cs.gptmusic.api

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient

class APIHandler {
    // http request client
    val client = OkHttpClient()
    val url = "https://ws.audioscrobbler.com"

    // firebase
    val database = Firebase.database
    val userDataRef = database.getReference("userdata")

    fun fetchUserInfo(email: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for(childSnapshot in dataSnapshot.children) {
                    val user = childSnapshot.getValue(UserData::class.java)
                    if(user?.email == email) {
                        User.info = user
                        break
                    }
                }
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                println("loadPost:onCancelled,  ${databaseError.toException()}")
            }
        }
        userDataRef.addValueEventListener(postListener)
    }

//    fun addKeys()
//    {
//        val map1 = mutableMapOf<String, Any>()
//        map1["uid"] = "v9EnGVlbiJPEQLHa5UfAYeGMzT32"
//        map1["apikey"] = "6d98cff955e3cbc1431919bcec6c6bf5"
//
//        val map2 = mutableMapOf<String, Any>()
//        map2["uid"] = "TwJAngetrJSRVzuygR01SnY9AmF3"
//        map2["apikey"] = "6d98cff955e3cbc1431919bcec6c6bf5"
//
//        val list = listOf<MutableMap<String, Any>>(map1, map2)
//        testRef.setValue(list)
//
//        testRef.child("1").child("uid").get().addOnSuccessListener {
//            println("Got value ${it.value}")
//        }.addOnFailureListener{
//            println("Error getting data")
//        }
//    }

    fun trackSearch(track: String){
        // get api key
    }
}