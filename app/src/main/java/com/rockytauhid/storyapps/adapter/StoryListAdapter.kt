package com.rockytauhid.storyapps.adapter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rockytauhid.storyapps.R
import com.rockytauhid.storyapps.data.database.StoryModel
import com.rockytauhid.storyapps.databinding.ItemRowStoryBinding
import com.rockytauhid.storyapps.helper.withDateFormat
import com.rockytauhid.storyapps.view.detail_story.DetailStoryActivity
import java.util.*

class StoryListAdapter :
    PagingDataAdapter<StoryModel, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryModel) {
            binding.apply {
                tvItemName.text = data.name
                tvItemCreatedAt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    data.createdAt.withDateFormat(TimeZone.getDefault().id)
                else
                    data.createdAt.take(10)

                Glide.with(itemView.context).load(data.photoUrl).into(ivItemPhoto)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(DetailStoryActivity.STORY_ID, data.id)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivItemPhoto, itemView.context.getString(R.string.image)),
                            Pair(tvItemName, itemView.context.getString(R.string.name)),
                            Pair(
                                tvItemCreatedAt,
                                itemView.context.getString(R.string.createdAt)
                            )
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryModel>() {
            override fun areItemsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryModel, newItem: StoryModel): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}