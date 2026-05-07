package com.maxdroid.lord.wschomeassignment.data.remote.dto

import com.maxdroid.lord.wschomeassignment.domain.model.League
import com.maxdroid.lord.wschomeassignment.domain.model.Match
import com.maxdroid.lord.wschomeassignment.domain.model.Team
import com.maxdroid.lord.wschomeassignment.domain.model.VideoClip

fun GameDto.toDomain(): Match? {
    // Filter: must have wscGame and primeStory
    val wscGame = this.wscGame ?: return null
    val primeStory = wscGame.primeStory ?: return null
    if (primeStory.pages.isEmpty()) return null
    
    // Get final score from last page
    val lastPage = primeStory.pages.lastOrNull()
    val homeScore = lastPage?.homeScore ?: 0
    val awayScore = lastPage?.awayScore ?: 0
    
    return Match(
        id = wscGameId,
        homeTeam = teams.home.toDomain(),
        awayTeam = teams.away.toDomain(),
        homeScore = homeScore,
        awayScore = awayScore,
        league = league.toDomain(),
        date = fixture.date,
        timestamp = fixture.timestamp,
        status = fixture.status.short,
        isLive = fixture.status.short == "LIVE" || fixture.status.short == "1H" || 
                 fixture.status.short == "2H" || fixture.status.short == "HT",
        videoClips = primeStory.pages.map { it.toDomain() }
    )
}

fun TeamDto.toDomain() = Team(
    id = id,
    name = name,
    logo = logo
)

fun LeagueDto.toDomain() = League(
    id = id,
    name = name,
    logo = logo,
    country = country,
    flag = flag
)

fun PageDto.toDomain() = VideoClip(
    id = pageId,
    videoUrl = videoUrl,
    title = title,
    homeScore = homeScore,
    awayScore = awayScore,
    gameClock = gameClock,
    period = period,
    actionType = actionType,
    duration = duration
)
