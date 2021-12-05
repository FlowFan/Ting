package com.example.ximalaya.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.ximalaya.RecommendViewModel
import com.example.ximalaya.adapters.FooterAdapter
import com.example.ximalaya.adapters.RecommendListAdapter
import com.example.ximalaya.databinding.FragmentRecommendBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    private val binding get() = _binding!!
    private val recommendListAdapter by lazy { RecommendListAdapter() }

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
        val viewModel by viewModels<RecommendViewModel>()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.albumList.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                recommendListAdapter.submitData(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            recommendListAdapter.loadStateFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest {
                    when (it.refresh) {
                        is LoadState.NotLoading -> {
                            delay(800)
                            binding.swipeRefresh.isRefreshing = false
                        }
                        is LoadState.Loading -> binding.swipeRefresh.isRefreshing = true
                        is LoadState.Error -> {
                            delay(3000)
                            binding.swipeRefresh.isRefreshing = false
                            recommendListAdapter.refresh()
                                .run { binding.swipeRefresh.isRefreshing = true }
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