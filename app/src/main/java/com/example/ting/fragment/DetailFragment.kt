package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import coil.load
import coil.transform.BlurTransformation
import com.example.ting.R
import com.example.ting.adapter.DetailListAdapter
import com.example.ting.databinding.FragmentDetailBinding
import com.example.ting.viewmodel.TingViewModel

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailFragmentArgs>()
    private val viewModel by activityViewModels<TingViewModel>()
    private val detailListAdapter by lazy { DetailListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.apply {
            adapter = detailListAdapter

        }
        args.album?.apply {
            binding.appBarImage.load(coverUrl) {
                transformations(BlurTransformation(requireContext()))
            }
            binding.image.load(coverUrl)
            binding.albumTitleTv.text = albumTitle
            binding.albumAuthorTv.text = albumIntro
            viewModel.getTrackList(id).observe(viewLifecycleOwner) {
                detailListAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}