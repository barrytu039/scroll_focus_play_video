package com.barrytu.focus_play

import android.net.Uri
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util

object ExoplayerUtil {

    private var simpleExoPlayer : SimpleExoPlayer? = null

    private val timeRecordMap : HashMap<String, Long> = HashMap()

    private var currentKey : String = ""

    fun playVideo(progressBar : View, thumbnailView : ImageView?, playerView : PlayerView, url : String) {
        // stop prevent video
        stopPlay()

        currentKey = url

        // init exo player
        val defaultLoadControl = DefaultLoadControl.Builder()
            .setPrioritizeTimeOverSizeThresholds(false).build()

        simpleExoPlayer = SimpleExoPlayer.Builder(AppApplication.getAppContext())
            .setLoadControl(defaultLoadControl)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
            }

        val dataSourceFactory =
            DefaultDataSourceFactory(AppApplication.getAppContext(), Util.getUserAgent(AppApplication.getAppContext(), AppApplication.getAppContext().getString(R.string.app_name)))

        val cacheDataSourceFactory = CacheDataSource.Factory().apply {
            setCache(AppApplication.createSimpleCache())
            setUpstreamDataSourceFactory(dataSourceFactory)
        }

        val videoSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

        simpleExoPlayer?.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_READY) {
                    fadeOutAnim(thumbnailView)
                    progressBar.visibility = View.GONE
                }
                if (state == Player.STATE_BUFFERING) {
                    progressBar.visibility = View.VISIBLE
                }
            }
        })

        simpleExoPlayer?.playWhenReady = true

        simpleExoPlayer?.setMediaSource(videoSource)

        playerView.player = simpleExoPlayer
        // check record video position
        if (timeRecordMap.containsKey(url)) {
            timeRecordMap[url]?.let { position ->
                simpleExoPlayer?.seekTo(position)
            }
        }

        simpleExoPlayer?.prepare()
    }

    fun stopPlay() {
        simpleExoPlayer?.let {
            if (it.isPlaying) {
                // record video position
                timeRecordMap[currentKey] = it.currentPosition
                it.stop()
            }
            simpleExoPlayer?.release()
        }
    }

    fun turnOnVolume() {
        simpleExoPlayer?.volume = 1f
    }

    fun turnOffVolume() {
        simpleExoPlayer?.volume = 0f
    }


    fun fadeOutAnim(view: View?) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.duration = 500
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                view?.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
        })
        view?.startAnimation(fadeOut)
    }
}