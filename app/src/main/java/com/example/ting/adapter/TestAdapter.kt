package com.example.ting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ting.databinding.ItemFooterBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 *
 * @author fanshun
 * @date 2023/6/2 14:51
 */
class TestAdapter : ListAdapter<String, ViewHolder>(object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}) {
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(
            ItemFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(holder.absoluteAdapterPosition)
            onItemClick?.invoke(holder.absoluteAdapterPosition)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewBinding.textView.text = getItem(position)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemClick(onItemClick: (Int) -> Unit) {
        this.onItemClick = onItemClick
    }
}

class ViewHolder(val viewBinding: ItemFooterBinding) : RecyclerView.ViewHolder(viewBinding.root)

interface OnItemClickListener {
    fun onItemClick(position: Int)
}

suspend fun TestAdapter.awaitItemClick() = suspendCancellableCoroutine { con ->
    val listener = object : OnItemClickListener {
        override fun onItemClick(position: Int) {
            con.resume(position)
            con.invokeOnCancellation {
                con.cancel(it)
            }
        }
    }
    setOnItemClickListener(listener)
}

fun View.clickFlow() = callbackFlow {
    setOnClickListener {
        trySend(it)
    }
    awaitClose { setOnClickListener(null) }
}