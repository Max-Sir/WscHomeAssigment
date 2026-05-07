package com.maxdroid.lord.wschomeassignment.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.maxdroid.lord.wschomeassignment.presentation.leagues.LeaguesScreen
import com.maxdroid.lord.wschomeassignment.presentation.player.StoryPlayerActivity
import com.maxdroid.lord.wschomeassignment.presentation.theme.WSCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            WSCTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LeaguesScreen(
                        onMatchClick = { matchId ->
                            navigateToPlayer(matchId)
                        }
                    )
                }
            }
        }
    }
    
    private fun navigateToPlayer(matchId: String) {
        val intent = Intent(this, StoryPlayerActivity::class.java).apply {
            putExtra(StoryPlayerActivity.EXTRA_MATCH_ID, matchId)
        }
        startActivity(intent)
    }
}
