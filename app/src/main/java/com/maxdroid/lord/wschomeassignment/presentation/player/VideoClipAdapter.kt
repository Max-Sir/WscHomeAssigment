package com.maxdroid.lord.wschomeassignment.presentation.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.maxdroid.lord.wschomeassignment.databinding.ItemVideoClipBinding
import com.maxdroid.lord.wschomeassignment.domain.model.VideoClip

class VideoClipAdapter(
    private val clips: List<VideoClip>,
    private val onClipChanged: (Int, VideoClip) -> Unit
) : RecyclerView.Adapter<VideoClipAdapter.VideoClipViewHolder>() {
    
    private var currentPlayer: ExoPlayer? = null
    private var currentPosition = 0
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoClipViewHolder {
        val binding = ItemVideoClipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoClipViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: VideoClipViewHolder, position: Int) {
        holder.bind(clips[position])
    }
    
    override fun getItemCount(): Int = clips.size
    
    fun playVideo(position: Int, player: ExoPlayer) {
        if (position == currentPosition && currentPlayer == player) {
            return
        }
        
        currentPlayer?.stop()
        currentPlayer = player
        currentPosition = position
        
        val clip = clips[position]
        val mediaItem = MediaItem.fromUri(clip.videoUrl)
        
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
        currentPlayer?.release()
        currentPlayer = null
    }
    
    inner class VideoClipViewHolder(
        private val binding: ItemVideoClipBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(clip: VideoClip) {
            // Binding is handled by the activity when the page is selected
        }
        
        fun getPlayerView() = binding.playerView
    }
}
