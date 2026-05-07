package com.maxdroid.lord.wschomeassignment.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GamesResponse(
    @Json(name = "response")
    val response: List<GameDto>
)

@JsonClass(generateAdapter = true)
data class GameDto(
    @Json(name = "WSCGameId")
    val wscGameId: String,
    @Json(name = "teams")
    val teams: TeamsDto,
    @Json(name = "league")
    val league: LeagueDto,
    @Json(name = "fixture")
    val fixture: FixtureDto,
    @Json(name = "wscGame")
    val wscGame: WscGameDto?
)

@JsonClass(generateAdapter = true)
data class TeamsDto(
    @Json(name = "home")
    val home: TeamDto,
    @Json(name = "away")
    val away: TeamDto
)

@JsonClass(generateAdapter = true)
data class TeamDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "logo")
    val logo: String
)

@JsonClass(generateAdapter = true)
data class LeagueDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "logo")
    val logo: String,
    @Json(name = "country")
    val country: String,
    @Json(name = "flag")
    val flag: String
)

@JsonClass(generateAdapter = true)
data class FixtureDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "date")
    val date: String,
    @Json(name = "timestamp")
    val timestamp: Long,
    @Json(name = "status")
    val status: StatusDto
)

@JsonClass(generateAdapter = true)
data class StatusDto(
    @Json(name = "short")
    val short: String,
    @Json(name = "long")
    val long: String
)

@JsonClass(generateAdapter = true)
data class WscGameDto(
    @Json(name = "gameId")
    val gameId: String,
    @Json(name = "homeTeamName")
    val homeTeamName: String,
    @Json(name = "awayTeamName")
    val awayTeamName: String,
    @Json(name = "primeStory")
    val primeStory: PrimeStoryDto?
)

@JsonClass(generateAdapter = true)
data class PrimeStoryDto(
    @Json(name = "storyId")
    val storyId: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "pages")
    val pages: List<PageDto>
)

@JsonClass(generateAdapter = true)
data class PageDto(
    @Json(name = "paggeId")
    val pageId: String,
    @Json(name = "videoUrl")
    val videoUrl: String,
    @Json(name = "title")
    val title: String?,
    @Json(name = "homeScore")
    val homeScore: Int?,
    @Json(name = "awayScore")
    val awayScore: Int?,
    @Json(name = "gameClock")
    val gameClock: String?,
    @Json(name = "period")
    val period: String?,
    @Json(name = "actionType")
    val actionType: String?,
    @Json(name = "duration")
    val duration: Int
)
