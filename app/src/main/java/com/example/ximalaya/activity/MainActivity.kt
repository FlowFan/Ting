package com.example.ximalaya.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ximalaya.R
import com.example.ximalaya.databinding.ActivityMainBinding
import com.example.ximalaya.fragment.MainFragment
import com.example.ximalaya.other.Constants.KEY_FIRST_START
import com.example.ximalaya.other.dataStore
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val dataStore by lazy { applicationContext.dataStore }
    private var isFirstStart: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking(Dispatchers.IO) {
            isFirstStart = dataStore.data.map {
                it[booleanPreferencesKey(KEY_FIRST_START)] ?: true
            }.first()
        }
        installSplashScreen().apply {
            setOnExitAnimationListener { splashScreen ->
                ObjectAnimator.ofFloat(
                    splashScreen.iconView,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreen.view.height.toFloat()
                ).apply {
                    interpolator = AnticipateInterpolator()
                    duration = 500L
                    doOnEnd {
                        splashScreen.remove()
                    }
                    if (isFirstStart) {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.toast_first_start),
                            Toast.LENGTH_SHORT
                        ).show()
                        splashScreen.view.setOnClickListener {
                            start()
                            lifecycleScope.launch(Dispatchers.IO) {
                                dataStore.edit {
                                    it[booleanPreferencesKey(KEY_FIRST_START)] = false
                                }
                            }
                        }
                    } else {
                        start()
                    }
                }
            }
        }
        setContentView(binding.root)
        val tabTitles = resources.getStringArray(R.array.tab_title)
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = tabTitles.size

            override fun createFragment(position: Int): Fragment {
                return MainFragment()
            }
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}