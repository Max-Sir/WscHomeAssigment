package com.maxdroid.lord.wschomeassignment.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxdroid.lord.wschomeassignment.domain.model.Match
import com.maxdroid.lord.wschomeassignment.domain.repository.GamesRepository
import com.maxdroid.lord.wschomeassignment.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StoryPlayerViewModel @Inject constructor(
    private val repository: GamesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()
    
    fun loadMatch(matchId: String) {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            
            when (val result = repository.getMatches()) {
                is Result.Success -> {
                    val match = result.data.find { it.id == matchId }
                    if (match != null) {
                        _uiState.value = PlayerUiState.Success(match)
                        Timber.d("Loaded match: ${match.homeTeam.name} vs ${match.awayTeam.name}")
                    } else {
                        _uiState.value = PlayerUiState.Error("Match not found")
                        Timber.e("Match not found: $matchId")
                    }
                }
                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Unknown error occurred"
                    _uiState.value = PlayerUiState.Error(errorMessage)
                    Timber.e(result.exception, "Error loading match")
                }
            }
        }
    }
}

sealed class PlayerUiState {
    object Loading : PlayerUiState()
    data class Success(val match: Match) : PlayerUiState()
    data class Error(val message: String) : PlayerUiState()
}
