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
                // Group by league ID first to avoid duplicate league objects
                val groupedById = result.data.groupBy { it.league.id }
                
                // Convert to Map<League, List<Match>> using the first league object from each group
                val groupedMatches = groupedById.mapKeys { (_, matches) ->
                    matches.first().league
                }
                
                // Sort leagues by number of matches (descending), then by name
                val sortedGroupedMatches = groupedMatches.toList()
                    .sortedWith(
                        compareByDescending<Pair<League, List<Match>>> { it.second.size }
                            .thenBy { it.first.name }
                    )
                    .toMap(LinkedHashMap())
                
                Result.Success(sortedGroupedMatches)
            }
            is Result.Error -> result
        }
    }
}
