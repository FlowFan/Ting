package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import coil.load
import com.example.ting.R
import com.example.ting.adapter.DetailListAdapter
import com.example.ting.databinding.FragmentDetailBinding
import com.example.ting.other.BlurTransformation
import com.example.ting.other.collectWithLifecycle
import com.example.ting.other.setOnItemClickListener
import com.example.ting.viewmodel.TingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailFragmentArgs>()
    private val viewModel by viewModels<TingViewModel>()
    private val detailListAdapter by lazy { DetailListAdapter() }
    private var coverUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared)
        args.album?.let { album ->
            viewModel.setDetailId(album.albumId)
            coverUrl = album.coverUrl
        }
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
        with(viewLifecycleOwner) {
            viewModel.trackList.collectWithLifecycle {
                detailListAdapter.submitData(it)
            }
        }
        with(binding.recyclerView) {
            adapter = detailListAdapter
            setOnItemClickListener { _, _ ->
                viewModel.play(
                    *detailListAdapter.snapshot().items.map {
                        MediaItem
                            .Builder()
                            .setMediaId("${it.id}")
                            .setUri(it.downloadUrl.toUri())
                            .setMediaMetadata(
                                MediaMetadata
                                    .Builder()
                                    .setTitle(it.trackTitle)
                                    .setArtworkUri(it.coverUrl.toUri())
                                    .build()
                            )
                            .build()
                    }.toTypedArray()
                )
            }
        }
        binding.appBarImage.load(coverUrl) {
            transformations(BlurTransformation(requireContext()))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}