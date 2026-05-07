package com.maxdroid.lord.wschomeassignment.presentation.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.maxdroid.lord.wschomeassignment.databinding.ItemVideoClipBinding
import com.maxdroid.lord.wschomeassignment.domain.model.VideoClip
import timber.log.Timber

class VideoClipAdapter(
    private val clips: List<VideoClip>,
    private val onClipChanged: (Int, VideoClip) -> Unit
) : RecyclerView.Adapter<VideoClipAdapter.VideoClipViewHolder>() {
    
    private var currentPlayer: ExoPlayer? = null
    private var currentPosition = -1
    private var currentPlayerView: androidx.media3.ui.PlayerView? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoClipViewHolder {
        val binding = ItemVideoClipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoClipViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: VideoClipViewHolder, position: Int) {
        // Binding handled when video plays
    }
    
    override fun getItemCount(): Int = clips.size
    
    fun playVideo(position: Int, player: ExoPlayer, playerView: androidx.media3.ui.PlayerView) {
        Timber.d("playVideo called for position $position")
        
        // If same position and already playing, do nothing
        if (position == currentPosition && currentPlayer == player && player.isPlaying) {
            Timber.d("Already playing at position $position")
            return
        }
        
        // Stop current playback
        currentPlayer?.stop()
        
        // Detach player from old view
        currentPlayerView?.player = null
        
        // Update references
        currentPlayer = player
        currentPosition = position
        currentPlayerView = playerView
        
        val clip = clips[position]
        val mediaItem = MediaItem.fromUri(clip.videoUrl)
        
        Timber.d("Loading video: ${clip.videoUrl}")
        
        // Attach player to new view FIRST
        playerView.player = player
        
        // Then load and play
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
        
        onClipChanged(position, clip)
    }
    
    fun pauseVideo() {
        currentPlayer?.pause()
    }
    
    fun resumeVideo() {
        currentPlayer?.play()
    }
    
    fun releasePlayer() {
        currentPlayerView?.player = null
        currentPlayer?.release()
        currentPlayer = null
        currentPlayerView = null
        currentPosition = -1
    }
    
    inner class VideoClipViewHolder(
        private val binding: ItemVideoClipBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun getPlayerView() = binding.playerView
    }
}
