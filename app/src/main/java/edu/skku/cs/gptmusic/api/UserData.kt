package edu.skku.cs.gptmusic.api

data class UserData(
    var apikey: String = "",
    var email: String = "",
    var uid: String = ""
)

object User{
    var info = UserData()
}