package com.maxdroid.lord.wschomeassignment.presentation.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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
        // ViewHolder ready for playback
    }
    
    override fun getItemCount(): Int = clips.size
    
    fun playVideo(position: Int, player: ExoPlayer, playerView: androidx.media3.ui.PlayerView) {
        if (position == currentPosition && currentPlayer == player && player.isPlaying) {
            return
        }
        
        currentPlayer?.pause()
        
        if (currentPlayerView != playerView) {
            currentPlayerView?.player = null
        }
        
        currentPlayer = player
        currentPosition = position
        currentPlayerView = playerView
        
        val clip = clips[position]
        playerView.player = player
        
        val currentMediaItem = player.currentMediaItem
        val targetUri = clip.videoUrl
        
        if (currentMediaItem?.localConfiguration?.uri?.toString() != targetUri) {
            val mediaItem = MediaItem.fromUri(targetUri)
            player.setMediaItem(mediaItem)
            player.prepare()
        } else {
            player.seekTo(0)
        }
        
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
