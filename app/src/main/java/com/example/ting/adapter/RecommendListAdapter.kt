package com.example.ting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.ting.R
import com.example.ting.databinding.ItemRecommendBinding
import com.example.ting.model.Album

class RecommendListAdapter : PagingDataAdapter<Album, RecommendListAdapter.RecommendListViewHolder>(
    object : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem
    }) {
    inner class RecommendListViewHolder(val binding: ItemRecommendBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecommendListViewHolder(ItemRecommendBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecommendListViewHolder, position: Int) {
        val a = getItem(position)
        holder.binding.album = a ?: return
    }
}

@BindingAdapter("bindingImage")
fun bindingImage(imageView: ImageView, url: String?) {
    imageView.load(url) {
        crossfade(1000)
        placeholder(R.drawable.shape_r13_white)
        transformations(RoundedCornersTransformation(20f))
    }
}