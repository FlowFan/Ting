package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.ting.R
import com.example.ting.activity.MainActivity
import com.example.ting.databinding.FragmentMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { binding.fragmentContainerView.getFragment<NavHostFragment>().navController }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (navController.currentDestination?.id == R.id.recommendFragment) {
                Toast.makeText(requireContext(), getString(R.string.toast_quit), Toast.LENGTH_SHORT)
                    .show()
                isEnabled = false
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(1500)
                    isEnabled = true
                }
            } else {
                navController.navigateUp()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController.addOnDestinationChangedListener { nav, destination, _ ->
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                if (destination.id != R.id.recommendFragment) {
                    nav.navigateUp()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_quit),
                        Toast.LENGTH_SHORT
                    ).show()
                    isEnabled = false
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(1500)
                        isEnabled = true
                    }
                }
            }
            (requireActivity() as MainActivity).binding.tabLayout.isVisible =
                destination.id == R.id.recommendFragment
            (requireActivity() as MainActivity).binding.imageView.isVisible =
                destination.id == R.id.recommendFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}