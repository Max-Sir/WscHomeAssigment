package com.maxdroid.lord.wschomeassignment.presentation.player

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
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
    private var prefetchPlayer: ExoPlayer? = null
    private var adapter: VideoClipAdapter? = null
    private var currentMatch: Match? = null
    
    private val progressBars = mutableListOf<ProgressBar>()
    private lateinit var gestureDetector: GestureDetector
    
    private var progressUpdateRunnable: Runnable? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    
    companion object {
        const val EXTRA_MATCH_ID = "match_id"
        private const val SEEK_TIME_MS = 10000L
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
        
        setupGestures()
    }
    
    private fun setupGestures() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                togglePlayPause()
                return true
            }
            
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val screenWidth = binding.root.width
                val tapX = e.x
                
                if (tapX < screenWidth / 2) {
                    seekBackward()
                } else {
                    seekForward()
                }
                return true
            }
            
            override fun onLongPress(e: MotionEvent) {
                adapter?.pauseVideo()
            }
        })
        
        binding.viewPager.setOnTouchListener { view, event ->
            val gestureHandled = gestureDetector.onTouchEvent(event)
            
            if (!gestureHandled && event.action == MotionEvent.ACTION_MOVE) {
                view.onTouchEvent(event)
            } else {
                gestureHandled
            }
        }
    }
    
    private fun togglePlayPause() {
        player?.let { exoPlayer ->
            if (exoPlayer.isPlaying) {
                adapter?.pauseVideo()
            } else {
                adapter?.resumeVideo()
            }
        }
    }
    
    private fun seekForward() {
        player?.let { exoPlayer ->
            val currentPosition = exoPlayer.currentPosition
            val duration = exoPlayer.duration
            val newPosition = (currentPosition + SEEK_TIME_MS).coerceAtMost(duration)
            exoPlayer.seekTo(newPosition)
        }
    }
    
    private fun seekBackward() {
        player?.let { exoPlayer ->
            val currentPosition = exoPlayer.currentPosition
            val newPosition = (currentPosition - SEEK_TIME_MS).coerceAtLeast(0)
            exoPlayer.seekTo(newPosition)
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
        
        binding.matchTitle.text = getString(R.string.match_title, match.homeTeam.name, match.awayTeam.name)
        
        // Initialize main player with optimized buffering
        player = ExoPlayer.Builder(this)
            .setLoadControl(
                androidx.media3.exoplayer.DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        8000,   // Min buffer
                        25000,  // Max buffer
                        500,    // Playback start threshold
                        1000    // Playback after rebuffer
                    )
                    .setPrioritizeTimeOverSizeThresholds(true)
                    .build()
            )
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = false
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                binding.loadingIndicator.visibility = View.VISIBLE
                            }
                            Player.STATE_READY -> {
                                binding.loadingIndicator.visibility = View.GONE
                                if (this@apply.isPlaying) {
                                    val currentPos = binding.viewPager.currentItem
                                    if (currentPos < progressBars.size) {
                                        startProgressUpdates(progressBars[currentPos])
                                    }
                                }
                            }
                            Player.STATE_ENDED -> {
                                val currentItem = binding.viewPager.currentItem
                                if (currentItem < match.videoClips.size - 1) {
                                    binding.viewPager.setCurrentItem(currentItem + 1, true)
                                } else {
                                    // Last video finished, return to leagues screen
                                    finish()
                                }
                            }
                            else -> {}
                        }
                    }
                    
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            val currentPos = binding.viewPager.currentItem
                            if (currentPos < progressBars.size) {
                                startProgressUpdates(progressBars[currentPos])
                            }
                        } else {
                            stopProgressUpdates()
                        }
                    }
                })
            }
        
        // Pre-load first video before UI creation for instant playback
        if (match.videoClips.isNotEmpty()) {
            val firstClip = match.videoClips[0]
            val mediaItem = androidx.media3.common.MediaItem.fromUri(firstClip.videoUrl)
            player?.setMediaItem(mediaItem)
            player?.prepare()
        }
        
        // Initialize prefetch player for background loading
        prefetchPlayer = ExoPlayer.Builder(this)
            .setLoadControl(
                androidx.media3.exoplayer.DefaultLoadControl.Builder()
                    .setBufferDurationsMs(5000, 15000, 500, 1000)
                    .build()
            )
            .build()
            .apply {
                playWhenReady = false
                volume = 0f
            }
        
        setupProgressIndicators(match.videoClips.size)
        
        adapter = VideoClipAdapter(match.videoClips) { position, clip ->
            updateProgressIndicators(position)
            updateClipInfo(clip)
        }
        
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 2
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var previousPosition = 0
            
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        adapter?.pauseVideo()
                    }
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        val currentPosition = binding.viewPager.currentItem
                        if (currentPosition == previousPosition) {
                            adapter?.resumeVideo()
                        }
                    }
                }
            }
            
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                previousPosition = position
                playClipAtPosition(position)
                
                if (position + 1 < match.videoClips.size) {
                    prefetchNextVideo(position + 1)
                }
            }
        })
        
        binding.viewPager.post {
            playClipAtPosition(0)
            if (match.videoClips.size > 1) {
                prefetchNextVideo(1)
            }
        }
    }
    
    private fun playClipAtPosition(position: Int) {
        player?.let { exoPlayer ->
            val recyclerView = binding.viewPager.getChildAt(0) as? androidx.recyclerview.widget.RecyclerView
            val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position) as? VideoClipAdapter.VideoClipViewHolder
            
            if (viewHolder != null) {
                updateProgressIndicators(position)
                adapter?.playVideo(position, exoPlayer, viewHolder.getPlayerView())
            } else {
                binding.viewPager.postDelayed({
                    playClipAtPosition(position)
                }, 100)
            }
        }
    }
    
    private fun prefetchNextVideo(position: Int) {
        currentMatch?.let { match ->
            if (position < match.videoClips.size) {
                val clip = match.videoClips[position]
                prefetchPlayer?.let { player ->
                    val mediaItem = androidx.media3.common.MediaItem.fromUri(clip.videoUrl)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                }
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
    
    private fun startProgressUpdates(progressBar: ProgressBar) {
        stopProgressUpdates()
        
        progressUpdateRunnable = object : Runnable {
            override fun run() {
                player?.let { exoPlayer ->
                    if (exoPlayer.duration > 0) {
                        val progress = ((exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()) * 100).toInt()
                        progressBar.progress = progress.coerceIn(0, 100)
                    }
                }
                handler.postDelayed(this, 100)
            }
        }
        handler.post(progressUpdateRunnable!!)
    }
    
    private fun stopProgressUpdates() {
        progressUpdateRunnable?.let { runnable ->
            handler.removeCallbacks(runnable)
        }
        progressUpdateRunnable = null
    }
    
    private fun updateClipInfo(clip: VideoClip) {
        binding.clipTitle.text = clip.title ?: ""
    }
    
    override fun onPause() {
        super.onPause()
        adapter?.pauseVideo()
        stopProgressUpdates()
    }
    
    override fun onResume() {
        super.onResume()
        adapter?.resumeVideo()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopProgressUpdates()
        adapter?.releasePlayer()
        player?.release()
        player = null
        prefetchPlayer?.release()
        prefetchPlayer = null
    }
}
