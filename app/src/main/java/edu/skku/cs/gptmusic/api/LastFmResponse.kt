package edu.skku.cs.gptmusic.api

// track search
data class LastFmTrackSearchResponse(
    val artist: String,
    val track: String,
    val listeners: Int
    // Add other required properties
)