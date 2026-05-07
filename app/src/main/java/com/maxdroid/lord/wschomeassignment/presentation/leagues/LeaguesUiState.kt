package com.maxdroid.lord.wschomeassignment.presentation.leagues

import com.maxdroid.lord.wschomeassignment.domain.model.League
import com.maxdroid.lord.wschomeassignment.domain.model.Match

sealed class LeaguesUiState {
    object Loading : LeaguesUiState()
    data class Success(val matchesByLeague: Map<League, List<Match>>) : LeaguesUiState()
    data class Error(val message: String) : LeaguesUiState()
}
