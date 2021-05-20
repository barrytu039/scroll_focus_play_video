package com.barrytu.focus_play

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barrytu.focus_play.databinding.ItemFocusplayBinding
import com.bumptech.glide.Glide

class FocusPlayListAdapter : RecyclerView.Adapter<FocusPlayListAdapter.PlayViewHolder>() {

    val mediaEntityMutableList : MutableList<MediaEntity> = mutableListOf()

    enum class FocusPlayStateEnum {
        PlayVideo,
        ShowThumbnail
    }

    fun updateDataSet(newDataSet : MutableList<MediaEntity>) {
        mediaEntityMutableList.clear()
        mediaEntityMutableList.addAll(newDataSet)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayViewHolder {
        return PlayViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_focusplay, parent, false))
    }

    override fun onBindViewHolder(holder: PlayViewHolder, position: Int) {
        holder.bind(mediaEntityMutableList[position].url)
    }

    override fun onBindViewHolder(
        holder: PlayViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        if (payloads[0] == FocusPlayStateEnum.PlayVideo) {
            holder.playMedia()
        }
        if (payloads[0] == FocusPlayStateEnum.ShowThumbnail) {
            holder.showThumbnail()
        }
    }

    override fun getItemCount(): Int {
        return mediaEntityMutableList.size
    }


    inner class PlayViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemFocusplayBinding.bind(itemView)

        var url : String = ""

        fun bind(url : String) {
            this.url = url
            showThumbnail()
            binding.itemFocusPlayProgressBar.visibility = View.GONE
        }

        fun showThumbnail() {
            Glide.with(binding.itemFocusPlayThumbnailImageVew).load("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg").into(binding.itemFocusPlayThumbnailImageVew)
            binding.itemFocusPlayThumbnailImageVew.visibility = View.VISIBLE
        }

        fun playMedia() {
            ExoplayerUtil.playVideo(binding.itemFocusPlayProgressBar,binding.itemFocusPlayThumbnailImageVew,binding.itemFocusPlayPlayerView, url)
        }

    }
}