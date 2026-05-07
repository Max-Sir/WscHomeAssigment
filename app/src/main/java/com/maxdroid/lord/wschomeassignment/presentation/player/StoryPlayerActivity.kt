package com.maxdroid.lord.wschomeassignment.presentation.player

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.viewpager2.widget.ViewPager2
import com.maxdroid.lord.wschomeassignment.R
import com.maxdroid.lord.wschomeassignment.databinding.ActivityStoryPlayerBinding
import com.maxdroid.lord.wschomeassignment.domain.model.Match
import com.maxdroid.lord.wschomeassignment.domain.model.VideoClip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class StoryPlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStoryPlayerBinding
    private val viewModel: StoryPlayerViewModel by viewModels()
    
    private var player: ExoPlayer? = null
    private var adapter: VideoClipAdapter? = null
    private var currentMatch: Match? = null
    
    private val progressBars = mutableListOf<ProgressBar>()
    
    companion object {
        const val EXTRA_MATCH_ID = "match_id"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupFullscreen()
        setupUI()
        observeViewModel()
        
        val matchId = intent.getStringExtra(EXTRA_MATCH_ID)
        if (matchId != null) {
            viewModel.loadMatch(matchId)
        } else {
            finish()
        }
    }
    
    private fun setupFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, binding.root)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = 
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
    
    private fun setupUI() {
        binding.closeButton.setOnClickListener {
            finish()
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is PlayerUiState.Loading -> {
                            binding.loadingIndicator.visibility = View.VISIBLE
                        }
                        is PlayerUiState.Success -> {
                            binding.loadingIndicator.visibility = View.GONE
                            setupPlayer(state.match)
                        }
                        is PlayerUiState.Error -> {
                            binding.loadingIndicator.visibility = View.GONE
                            Timber.e("Error: ${state.message}")
                            finish()
                        }
                    }
                }
            }
        }
    }
    
    private fun setupPlayer(match: Match) {
        currentMatch = match
        
        // Setup match info
        binding.matchTitle.text = "${match.homeTeam.name} vs ${match.awayTeam.name}"
        
        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        // Auto-advance to next clip
                        val currentItem = binding.viewPager.currentItem
                        if (currentItem < match.videoClips.size - 1) {
                            binding.viewPager.setCurrentItem(currentItem + 1, true)
                        }
                    }
                }
            })
        }
        
        // Setup progress indicators
        setupProgressIndicators(match.videoClips.size)
        
        // Setup ViewPager2
        adapter = VideoClipAdapter(match.videoClips) { position, clip ->
            updateProgressIndicators(position)
            updateClipInfo(clip)
        }
        
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 1
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                // When user starts scrolling, pause current video
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    adapter?.pauseVideo()
                }
            }
            
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Timber.d("Page selected: $position")
                // Wait for layout to settle before playing
                binding.viewPager.post {
                    playClipAtPosition(position)
                }
            }
        })
        
        // Play first clip after layout is ready
        binding.viewPager.post {
            playClipAtPosition(0)
        }
    }
    
    private fun playClipAtPosition(position: Int) {
        player?.let { exoPlayer ->
            val recyclerView = binding.viewPager.getChildAt(0) as? androidx.recyclerview.widget.RecyclerView
            val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position) as? VideoClipAdapter.VideoClipViewHolder
            
            if (viewHolder != null) {
                Timber.d("Playing clip at position $position")
                adapter?.playVideo(position, exoPlayer, viewHolder.getPlayerView())
            } else {
                Timber.w("ViewHolder not found for position $position, retrying...")
                // Retry after a short delay
                binding.viewPager.postDelayed({
                    playClipAtPosition(position)
                }, 150)
            }
        }
    }
    
    private fun setupProgressIndicators(count: Int) {
        binding.progressContainer.removeAllViews()
        progressBars.clear()
        
        for (i in 0 until count) {
            val progressBar = ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
            ).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    8,
                    1f
                ).apply {
                    marginStart = if (i == 0) 0 else 4
                    marginEnd = if (i == count - 1) 0 else 4
                }
                max = 100
                progress = 0
                progressDrawable = getDrawable(R.drawable.progress_indicator)
            }
            
            binding.progressContainer.addView(progressBar)
            progressBars.add(progressBar)
        }
    }
    
    private fun updateProgressIndicators(currentPosition: Int) {
        progressBars.forEachIndexed { index, progressBar ->
            when {
                index < currentPosition -> {
                    progressBar.progress = 100
                    progressBar.progressDrawable = getDrawable(R.drawable.progress_indicator_active)
                }
                index == currentPosition -> {
                    progressBar.progress = 0
                    progressBar.progressDrawable = getDrawable(R.drawable.progress_indicator_active)
                }
                else -> {
                    progressBar.progress = 0
                    progressBar.progressDrawable = getDrawable(R.drawable.progress_indicator)
                }
            }
        }
    }
    
    private fun updateClipInfo(clip: VideoClip) {
        binding.clipTitle.text = clip.title ?: ""
    }
    
    override fun onPause() {
        super.onPause()
        adapter?.pauseVideo()
    }
    
    override fun onResume() {
        super.onResume()
        adapter?.resumeVideo()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        adapter?.releasePlayer()
        player?.release()
        player = null
    }
}
