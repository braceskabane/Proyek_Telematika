package com.dicoding.core.ui.adapter.story

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.core.R
import com.dicoding.core.domain.story.tester.model.StoryDomainTester
import com.dicoding.core.ui.adapter.story.callback.StoryPagingDiffCallback

class StoryPagingAdapter :
    PagingDataAdapter<StoryDomainTester, StoryPagingAdapter.ViewHolder>(StoryPagingDiffCallback()) {

    private var onItemClickCallback: OnItemClickCallback? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivItemPhotoUrl: ImageView = itemView.findViewById(R.id.iv_item_photo_url)
        private val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        private val tvItemCreatedAt: TextView = itemView.findViewById(R.id.tv_item_createdAt)
        private val tvItemDescription: TextView = itemView.findViewById(R.id.tv_item_description)

        fun bind(story: StoryDomainTester) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(ivItemPhotoUrl)

            tvItemName.text = story.name
            tvItemCreatedAt.text = story.createdAt
            tvItemDescription.text = story.description

            itemView.setOnClickListener {
                // Use the callback only if it is not null
                onItemClickCallback?.onItemClicked(story)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback?) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(story: StoryDomainTester)
    }
}


