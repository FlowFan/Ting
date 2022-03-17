package com.example.ting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ting.R
import com.example.ting.databinding.ItemFooterBinding

class FooterAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<FooterAdapter.FooterViewHolder>() {
    inner class FooterViewHolder(val binding: ItemFooterBinding) :
        RecyclerView.ViewHolder(binding.root)

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