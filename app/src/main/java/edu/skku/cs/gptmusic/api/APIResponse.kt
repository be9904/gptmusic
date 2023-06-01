package edu.skku.cs.gptmusic.api

import com.google.gson.annotations.SerializedName

// region Firebase User Info
data class FirebaseUserResponse(val users: List<UserData>)
// endregion

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
    val name: String = "",
    val artist: String = "",
    val url: String = "",
    val streamable: String = "",
    val listeners: String = "",
    val image: List<Image> = listOf(),
    val mbid: String = ""
)
data class Image(
    @SerializedName("#text")
    val text: String = "",
    val size: String = ""
)
class TrackSearchAttr
// endregion

// region Track Info Response
data class TrackInfoResponse(
    val track: TrackInfo
)

data class TrackInfo(
    val name: String,
    val mbid: String,
    val url: String,
    val duration: String,
    val streamable: Streamable,
    val listeners: String,
    val playcount: String,
    val artist: Artist,
    val album: Album,
    val toptags: TopTags,
    val wiki: Wiki
)

data class Streamable(
    val text: String,
    val fulltrack: String
)

data class Artist(
    val name: String,
    val mbid: String,
    val url: String
)

data class Album(
    val artist: String,
    val title: String,
    val mbid: String,
    val url: String,
    val image: List<Image>,
    @SerializedName("@attr")
    val attr: Attr
)

data class Attr(
    val position: String
)

data class TopTags(
    val tag: List<Tag>
)

data class Tag(
    val name: String,
    val url: String
)

data class Wiki(
    val published: String,
    val summary: String,
    val content: String
)
// endregion

// region ChatGPT Response
data class ChatCompletionResponse(
    val id: String,
    @SerializedName("object")
    val obj: String,
    val created: Long,
    val model: String,
    val usage: Usage,
    val choices: List<Choice>
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

data class Choice(
    val message: Message,
    val finish_reason: String,
    val index: Int
)

data class Message(
    val role: String,
    val content: String
)
// endregion