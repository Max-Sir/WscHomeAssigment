package com.maxdroid.lord.wschomeassignment.data.remote.api

import com.maxdroid.lord.wschomeassignment.data.remote.dto.GamesResponse
import retrofit2.http.GET

interface GamesApiService {
    @GET("tests/mobile-task/games.json")
    suspend fun getGames(): GamesResponse
}
