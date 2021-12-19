package com.example.ximalaya.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.ximalaya.viewmodel.RecommendViewModel
import com.example.ximalaya.adapter.FooterAdapter
import com.example.ximalaya.adapter.RecommendListAdapter
import com.example.ximalaya.databinding.FragmentRecommendBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RecommendViewModel>()
    private val recommendListAdapter by lazy {
        RecommendListAdapter {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recommendList.adapter =
            recommendListAdapter.withLoadStateFooter(FooterAdapter { (recommendListAdapter.retry()) })
        viewModel.albumList.observe(viewLifecycleOwner) {
            recommendListAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            recommendListAdapter.loadStateFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
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
        binding.swipeRefresh.setOnRefreshListener {
            recommendListAdapter.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}