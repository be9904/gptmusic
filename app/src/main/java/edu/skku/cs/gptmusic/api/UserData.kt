package edu.skku.cs.gptmusic.api

data class UserData(
    var apikey: String = "",
    var email: String = "",
    var uid: String = "",
    val savedTracks: List<Track> = listOf()
)

object User{
    var info = UserData()
}