package edu.skku.cs.gptmusic.api

import com.google.gson.annotations.SerializedName

// region Track Search Response
data class TrackSearchResponse(val results: TrackSearchResults)
data class TrackSearchResults(
    @SerializedName("opensearch:Query")
    val query: TrackSearchQuery,
    @SerializedName("opensearch:totalResults")
    val totalResults: String,
    @SerializedName("opensearch:startIndex")
    val startIndex: String,
    @SerializedName("opensearch:itemsPerPage")
    val itemsPerPage: String,
    val trackmatches: TrackMatches,
    @SerializedName("@attr")
    val attr: TrackSearchAttr
)
data class TrackSearchQuery(
    @SerializedName("#text")
    val text: String,
    val role: String,
    val startPage: String
)
data class TrackMatches(val track: List<Track>)
data class Track(
    val name: String,
    val artist: String,
    val url: String,
    val streamable: String,
    val listeners: String,
    val image: List<Image>,
    val mbid: String
)
data class Image(
    @SerializedName("#text")
    val text: String,
    val size: String
)
class TrackSearchAttr
// endregion