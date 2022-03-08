package com.example.ximalaya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.ximalaya.R
import com.example.ximalaya.databinding.FragmentMainBinding
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}