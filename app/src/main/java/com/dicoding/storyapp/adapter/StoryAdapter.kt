package com.dicoding.storyapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.model.Story
import com.dicoding.storyapp.activity.StoryDetailActivity

class StoryAdapter(private val stories: List<Story>, private val context: Context) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.iv_item_photo)
        val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tv_item_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.nameTextView.text = story.name
        holder.descriptionTextView.text = story.description

        // Muat gambar menggunakan Glide
        Glide.with(context)
            .load(story.photoUrl)
            .into(holder.photoImageView)

        // Klik listener untuk membuka halaman detail
        holder.itemView.setOnClickListener {
            val intent = Intent(context, StoryDetailActivity::class.java)
            intent.putExtra("name", story.name)
            intent.putExtra("photoUrl", story.photoUrl)
            intent.putExtra("description", story.description)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = stories.size
}
