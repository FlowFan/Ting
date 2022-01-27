package com.example.ximalaya.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.ximalaya.model.Album
import com.example.ximalaya.R
import com.example.ximalaya.databinding.ItemRecommendBinding

class RecommendListAdapter(private val onClick: () -> Unit) :
    PagingDataAdapter<Album, RecommendListAdapter.RecommendListViewHolder>(
        object : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem
        }) {
    inner class RecommendListViewHolder(val binding: ItemRecommendBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendListViewHolder {
        val holder = RecommendListViewHolder(
            ItemRecommendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holder.itemView.setOnClickListener { onClick() }
        return holder
    }

    override fun onBindViewHolder(holder: RecommendListViewHolder, position: Int) {
        getItem(position)?.let {
            holder.binding.album = it
        }
    }
}

@BindingAdapter("bindingImage")
fun bindingImage(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        imageView.load(url) {
            crossfade(1000)
            placeholder(R.drawable.shape_r13_white)
            transformations(RoundedCornersTransformation(30f))
        }
    }
}