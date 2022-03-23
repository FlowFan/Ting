package com.example.ting.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.ting.R
import com.example.ting.databinding.ActivityMainBinding
import com.example.ting.other.Constants.KEY_FIRST_START
import com.example.ting.other.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val dataStore by lazy { applicationContext.dataStore }
    private val ioDispatcher by lazy { Dispatchers.IO }
    private val navController by lazy { binding.fragmentContainerView.getFragment<NavHostFragment>().navController }
    private var isFirstStart: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking(ioDispatcher) {
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
                            lifecycleScope.launch(ioDispatcher) {
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
        navController.addOnDestinationChangedListener { _, destination, _ ->
            WindowCompat.setDecorFitsSystemWindows(window, destination.id != R.id.detailFragment)
            binding.floatingActionButton.isVisible = destination.id != R.id.playerFragment
        }
        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.playerFragment)
        }
        setContentView(binding.root)
    }
}