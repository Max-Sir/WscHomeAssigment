package com.maxdroid.lord.wschomeassignment.presentation.leagues

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.maxdroid.lord.wschomeassignment.R
import com.maxdroid.lord.wschomeassignment.domain.model.League
import com.maxdroid.lord.wschomeassignment.domain.model.Match
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LeaguesScreen(
    onMatchClick: (String) -> Unit,
    viewModel: LeaguesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.leagues)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is LeaguesUiState.Loading -> {
                    LoadingContent()
                }
                is LeaguesUiState.Success -> {
                    MatchesContent(
                        matchesByLeague = state.matchesByLeague,
                        onMatchClick = onMatchClick
                    )
                }
                is LeaguesUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.loadMatches() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(stringResource(R.string.loading_matches))
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.error_loading_matches),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun MatchesContent(
    matchesByLeague: Map<League, List<Match>>,
    onMatchClick: (String) -> Unit
) {
    val expandedLeagues = remember { mutableStateMapOf<Int, Boolean>().apply {
        matchesByLeague.keys.forEach { league ->
            this[league.id] = false
        }
    } }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        matchesByLeague.entries.forEachIndexed { index, (league, matches) ->
            item(key = "league_header_${league.id}_$index") {
                val isExpanded = expandedLeagues[league.id] ?: false
                LeagueHeader(
                    league = league,
                    isExpanded = isExpanded,
                    onToggle = {
                        expandedLeagues[league.id] = !isExpanded
                    }
                )
            }
            
            val isExpanded = expandedLeagues[league.id] ?: false
            if (isExpanded) {
                items(
                    items = matches,
                    key = { match -> "match_${match.id}" }
                ) { match ->
                    MatchCard(
                        match = match,
                        onClick = { onMatchClick(match.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LeagueHeader(
    league: League,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LeagueLogo(leagueName = league.name, leagueLogo = league.logo)
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = league.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = league.country,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LeagueLogo(leagueName: String, leagueLogo: String) {
    var isError by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (isError) {
            Text(
                text = leagueName.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        } else {
            AsyncImage(
                model = leagueLogo,
                contentDescription = leagueName,
                modifier = Modifier
                    .size(28.dp)
                    .padding(2.dp),
                contentScale = ContentScale.Crop,
                onError = { isError = true }
            )
        }
    }
}

@Composable
private fun MatchCard(
    match: Match,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Live indicator and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (match.isLive) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                        Text(
                            text = stringResource(R.string.live),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = formatDate(match.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = match.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Teams and score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home team
                TeamInfo(
                    teamName = match.homeTeam.name,
                    teamLogo = match.homeTeam.logo,
                    modifier = Modifier.weight(1f)
                )
                
                // Score
                Text(
                    text = "${match.homeScore} - ${match.awayScore}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                // Away team
                TeamInfo(
                    teamName = match.awayTeam.name,
                    teamLogo = match.awayTeam.logo,
                    modifier = Modifier.weight(1f),
                    isAway = true
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Clips count
            Text(
                text = stringResource(R.string.highlights, match.videoClips.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TeamInfo(
    teamName: String,
    teamLogo: String,
    modifier: Modifier = Modifier,
    isAway: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (isAway) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isAway) {
            TeamLogo(teamName = teamName, teamLogo = teamLogo)
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Text(
            text = teamName,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        if (isAway) {
            Spacer(modifier = Modifier.width(8.dp))
            TeamLogo(teamName = teamName, teamLogo = teamLogo)
        }
    }
}

@Composable
private fun TeamLogo(teamName: String, teamLogo: String) {
    var isError by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (isError) {
            Text(
                text = teamName.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            AsyncImage(
                model = teamLogo,
                contentDescription = teamName,
                modifier = Modifier
                    .size(36.dp)
                    .padding(2.dp),
                contentScale = ContentScale.Crop,
                onError = { isError = true }
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}
