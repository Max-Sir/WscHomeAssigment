package com.maxdroid.lord.wschomeassignment.domain.repository

import com.maxdroid.lord.wschomeassignment.domain.model.Match
import com.maxdroid.lord.wschomeassignment.util.Result

interface GamesRepository {
    suspend fun getMatches(): Result<List<Match>>
}
