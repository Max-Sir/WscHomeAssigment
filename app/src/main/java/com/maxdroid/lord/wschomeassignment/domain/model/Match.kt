package com.maxdroid.lord.wschomeassignment.domain.model

data class Match(
    val id: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int,
    val awayScore: Int,
    val league: League,
    val date: String,
    val timestamp: Long,
    val status: String,
    val isLive: Boolean,
    val videoClips: List<VideoClip>
)
