package com.example.picodiploma.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picodiploma.storyapp.R
import com.example.picodiploma.storyapp.data.response.Story
import com.example.picodiploma.storyapp.view.DetailActivity


class PagingCardAdapter : PagingDataAdapter<Story, PagingCardAdapter.PagingCardViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_paging_card, parent, false)
        return PagingCardViewHolder(view) { story: Story ->
            val intent = Intent(parent.context, DetailActivity::class.java).apply {
                putExtra("STORY_ID", story.id)
            }
            parent.context.startActivity(intent)
        }
    }

    override fun onBindViewHolder(holder: PagingCardViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    class PagingCardViewHolder(view: View, private val listener: (Story) -> Unit) : RecyclerView.ViewHolder(view) {
        private val imageViewMain: ImageView = view.findViewById(R.id.pagingImageView)
        private val textViewTitleMain: TextView = view.findViewById(R.id.pagingTitleTextView)
        private val textViewDescriptionMain: TextView = view.findViewById(R.id.pagingDescriptionTextView)

        fun bind(story: Story) {
            itemView.setOnClickListener { listener(story) } // <--- Set OnClickListener here
            textViewTitleMain.text = story.name
            textViewDescriptionMain.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.baseline_error_24)
                .into(imageViewMain)
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}


