package com.example.ximalaya

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ximalaya.databinding.ActivityMainBinding
import com.example.ximalaya.fragment.RecommendFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val tabTitles = resources.getStringArray(R.array.tab_title)
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = tabTitles.size

            override fun createFragment(position: Int): Fragment {
                return RecommendFragment()
            }
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}