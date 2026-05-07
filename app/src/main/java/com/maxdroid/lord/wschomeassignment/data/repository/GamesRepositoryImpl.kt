package com.maxdroid.lord.wschomeassignment.data.repository

import com.maxdroid.lord.wschomeassignment.data.remote.api.GamesApiService
import com.maxdroid.lord.wschomeassignment.data.remote.dto.toDomain
import com.maxdroid.lord.wschomeassignment.domain.model.Match
import com.maxdroid.lord.wschomeassignment.domain.repository.GamesRepository
import com.maxdroid.lord.wschomeassignment.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class GamesRepositoryImpl @Inject constructor(
    private val apiService: GamesApiService
) : GamesRepository {
    
    override suspend fun getMatches(): Result<List<Match>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getGames()
            val matches = response.response
                .mapNotNull { it.toDomain() } // Filter and map to domain
                .sortedByDescending { it.timestamp } // Sort by date, newest first
            
            Timber.d("Fetched ${matches.size} matches with highlights")
            Result.Success(matches)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching matches")
            Result.Error(e)
        }
    }
}
