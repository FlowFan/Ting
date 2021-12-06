package com.example.ximalaya.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ximalaya.R
import com.example.ximalaya.databinding.ItemFooterBinding

class FooterAdapter(private val retry: () -> Unit) : LoadStateAdapter<FooterViewHolder>() {
    override fun onBindViewHolder(holder: FooterViewHolder, loadState: LoadState) {
        holder.binding.apply {
            progressBar.isVisible = loadState is LoadState.Loading
            holder.itemView.isClickable = loadState is LoadState.Error
            when (loadState) {
                is LoadState.Loading -> textView.setText(R.string.footer_loading)
                is LoadState.Error -> textView.setText(R.string.footer_error)
                else -> textView.setText(R.string.footer_success)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): FooterViewHolder {
        val holder = FooterViewHolder(
            ItemFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holder.itemView.setOnClickListener { retry() }
        return holder
    }
}

class FooterViewHolder(val binding: ItemFooterBinding) : RecyclerView.ViewHolder(binding.root)