package com.dicoding.storyapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemRowStoryBinding

class StoryAdapter(
  private val listStory: ArrayList<ListStoryItem>,
  private val listener: OnItemClickCallback
): RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
  private lateinit var binding: ItemRowStoryBinding

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.ViewHolder {
    binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  class ViewHolder(private var binding: ItemRowStoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(story: ListStoryItem, listener: OnItemClickCallback) {
      Glide.with(binding.root.context)
        .load(story.photoUrl) // URL Avatar
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_broken_image)
        .into(binding.imgItemImage)
      binding.tvName.text = story.name
      binding.tvDescription.text = story.description

      // avatar OnClickListener
      binding.imgItemImage.setOnClickListener {
        listener.onItemClicked(story)
      }
    }
  }

  interface OnItemClickCallback {
    fun onItemClicked(data: ListStoryItem)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.onBind(listStory[position], listener)
  }

  override fun getItemCount() = listStory.size

}