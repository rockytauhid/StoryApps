/*
package com.rockytauhid.storyapps.view.story

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rockytauhid.storyapps.R
import com.rockytauhid.storyapps.data.remote.ListStoryItem
import com.rockytauhid.storyapps.helper.withDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListStoryAdapter (private val listStories: ArrayList<ListStoryItem>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_story, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStories[position])
    }

    override fun getItemCount(): Int = listStories.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var ivPhoto: ImageView = itemView.findViewById(R.id.iv_item_photo)
        private var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        private var tvCreatedAt: TextView = itemView.findViewById(R.id.tv_item_createdAt)

        fun bind(item: ListStoryItem) {
            tvName.text =  item.name
            tvCreatedAt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                item.createdAt.withDateFormat(TimeZone.getDefault().id)
            else
                item.createdAt.take(10)

            Glide.with(itemView.context).load(item.photoUrl).into(ivPhoto)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(STORIES, item)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(ivPhoto, itemView.context.getString(R.string.image)),
                        Pair(tvName, itemView.context.getString(R.string.name)),
                        Pair(tvCreatedAt, itemView.context.getString(R.string.createdAt))
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        const val STORIES = "stories"
    }
}

*/
