package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.ting.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailFragmentArgs>()

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
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        ViewCompat.getWindowInsetsController(view)?.systemBarsBehavior =
            BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}