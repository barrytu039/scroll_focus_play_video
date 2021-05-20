package com.barrytu.focus_play

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barrytu.focus_play.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    var focusPlayListAdapter : FocusPlayListAdapter? = null

    var lastScrollState = 0

    var lastPlayPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // init data set
        val dataSetMutableList = mutableListOf<MediaEntity>()

        dataSetMutableList.add(MediaEntity("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

        dataSetMutableList.add(MediaEntity("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"))
        dataSetMutableList.add(MediaEntity("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

        dataSetMutableList.add(MediaEntity("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"))
        dataSetMutableList.add(MediaEntity("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

        dataSetMutableList.add(MediaEntity("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"))
        dataSetMutableList.add(MediaEntity("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

        dataSetMutableList.add(MediaEntity("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"))
        dataSetMutableList.add(MediaEntity("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

        dataSetMutableList.add(MediaEntity("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"))

        // init rv
        binding.mainPlayRecycleView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (lastScrollState != newState) {
                        if (lastScrollState == RecyclerView.SCROLL_STATE_SETTLING || lastScrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                val lm = recyclerView.layoutManager
                                require(lm is LinearLayoutManager) { "Expected recyclerview to have linear layout manager" }
                                val position = lm.findFirstCompletelyVisibleItemPosition()
                                if (position != -1) {
                                    playItemPosition(position)
                                } else {
                                    focusPlayListAdapter?.notifyItemChanged(
                                        lastPlayPosition,
                                        FocusPlayListAdapter.FocusPlayStateEnum.ShowThumbnail
                                    )
                                    ExoplayerUtil.stopPlay()
                                }
                            }
                        }
                    }
                    lastScrollState = newState
                }
            })
        }

        // init adapter
        focusPlayListAdapter = FocusPlayListAdapter().apply {
            updateDataSet(dataSetMutableList)
            binding.mainPlayRecycleView.adapter = this
        }

    }

    private fun playItemPosition(position : Int) {
        if (position != lastPlayPosition) {
            // notify resume pre-item cover
            focusPlayListAdapter?.notifyItemChanged(
                lastPlayPosition,
                FocusPlayListAdapter.FocusPlayStateEnum.ShowThumbnail
            )
        }

        // notify play current position video
        focusPlayListAdapter?.notifyItemChanged(
            position,
            FocusPlayListAdapter.FocusPlayStateEnum.PlayVideo)
        lastPlayPosition = position
    }

    override fun onDestroy() {
        ExoplayerUtil.stopPlay()
        super.onDestroy()
    }
}