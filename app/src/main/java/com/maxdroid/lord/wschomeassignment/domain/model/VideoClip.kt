package com.maxdroid.lord.wschomeassignment.domain.model

data class VideoClip(
    val id: String,
    val videoUrl: String,
    val title: String?,
    val homeScore: Int?,
    val awayScore: Int?,
    val gameClock: String?,
    val period: String?,
    val actionType: String?,
    val duration: Int
)
