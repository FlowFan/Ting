package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.ting.R
import com.example.ting.adapter.FooterAdapter
import com.example.ting.adapter.RecommendListAdapter
import com.example.ting.databinding.FragmentRecommendBinding
import com.example.ting.other.collectLatestWithLifecycle
import com.example.ting.other.setOnItemClickListener
import com.example.ting.viewmodel.TingViewModel
import kotlinx.coroutines.delay

class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<TingViewModel>()
    private val recommendListAdapter by lazy { RecommendListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.albumList.observe(viewLifecycleOwner) {
            recommendListAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        with(viewLifecycleOwner) {
            recommendListAdapter.loadStateFlow.collectLatestWithLifecycle {
                when (it.refresh) {
                    //加载完成300ms后停止转动
                    is LoadState.NotLoading -> {
                        delay(300)
                        binding.swipeRefresh.isRefreshing = false
                    }
                    //正在加载时转动
                    is LoadState.Loading -> binding.swipeRefresh.isRefreshing = true
                    //加载失败时每隔3秒重试一次并重新转动
                    is LoadState.Error -> {
                        delay(3000)
                        binding.swipeRefresh.isRefreshing = false
                        recommendListAdapter.retry()
                    }
                }
            }
        }
        with(binding.recommendList) {
            adapter = recommendListAdapter.withLoadStateFooter(FooterAdapter())
            setOnItemClickListener { i, viewHolder ->
                if (viewHolder is RecommendListAdapter.RecommendListViewHolder) {
                    val action = MainFragmentDirections.actionMainFragmentToDetailFragment(recommendListAdapter.peek(i))
                    viewHolder.itemView.transitionName = getString(R.string.item_description)
                    viewHolder.binding.albumCover.transitionName = getString(R.string.image_description)
                    val extras = FragmentNavigatorExtras(
                        viewHolder.itemView to getString(R.string.item_description),
                        viewHolder.binding.albumCover to getString(R.string.image_description)
                    )
                    findNavController().navigate(action, extras)
                } else if (viewHolder is FooterAdapter.FooterViewHolder) {
                    recommendListAdapter.retry()
                }
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            recommendListAdapter.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recommendList.adapter = null
        _binding = null
    }
}