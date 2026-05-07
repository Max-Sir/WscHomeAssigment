package com.maxdroid.lord.wschomeassignment.presentation.leagues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxdroid.lord.wschomeassignment.domain.usecase.GetMatchesGroupedByLeagueUseCase
import com.maxdroid.lord.wschomeassignment.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LeaguesViewModel @Inject constructor(
    private val getMatchesGroupedByLeagueUseCase: GetMatchesGroupedByLeagueUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LeaguesUiState>(LeaguesUiState.Loading)
    val uiState: StateFlow<LeaguesUiState> = _uiState.asStateFlow()
    
    init {
        loadMatches()
    }
    
    fun loadMatches() {
        viewModelScope.launch {
            _uiState.value = LeaguesUiState.Loading
            
            when (val result = getMatchesGroupedByLeagueUseCase()) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.value = LeaguesUiState.Error("No matches with highlights available")
                    } else {
                        _uiState.value = LeaguesUiState.Success(result.data)
                        Timber.d("Loaded ${result.data.size} leagues")
                    }
                }
                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Unknown error occurred"
                    _uiState.value = LeaguesUiState.Error(errorMessage)
                    Timber.e(result.exception, "Error loading matches")
                }
            }
        }
    }
}
