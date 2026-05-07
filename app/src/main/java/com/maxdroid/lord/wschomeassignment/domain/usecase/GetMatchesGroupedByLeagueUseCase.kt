package com.maxdroid.lord.wschomeassignment.domain.usecase

import com.maxdroid.lord.wschomeassignment.domain.model.League
import com.maxdroid.lord.wschomeassignment.domain.model.Match
import com.maxdroid.lord.wschomeassignment.domain.repository.GamesRepository
import com.maxdroid.lord.wschomeassignment.util.Result
import javax.inject.Inject

class GetMatchesGroupedByLeagueUseCase @Inject constructor(
    private val repository: GamesRepository
) {
    suspend operator fun invoke(): Result<Map<League, List<Match>>> {
        return when (val result = repository.getMatches()) {
            is Result.Success -> {
                val groupedMatches = result.data.groupBy { it.league }
                Result.Success(groupedMatches)
            }
            is Result.Error -> result
        }
    }
}
