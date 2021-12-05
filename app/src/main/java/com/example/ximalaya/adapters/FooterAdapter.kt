package com.example.ximalaya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ximalaya.R
import com.example.ximalaya.databinding.ItemFooterBinding

class FooterAdapter(private val retry: () -> Unit) : LoadStateAdapter<FooterViewHolder>() {
    override fun onBindViewHolder(holder: FooterViewHolder, loadState: LoadState) {
        holder.binding.apply {
            when (loadState) {
                is LoadState.Loading -> {
                    textView.setText(R.string.footer_loading)
                    progressBar.visibility = View.VISIBLE
                    holder.itemView.isClickable = false
                }
                is LoadState.Error -> {
                    textView.setText(R.string.footer_error)
                    progressBar.visibility = View.GONE
                    holder.itemView.isClickable = true
                }
                else -> {
                    textView.setText(R.string.footer_success)
                    progressBar.visibility = View.GONE
                    holder.itemView.isClickable = false
                }
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