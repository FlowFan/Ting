package com.example.ting.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.ting.NavigationDirections
import com.example.ting.R
import com.example.ting.databinding.ActivityMainBinding
import com.example.ting.other.Constants.KEY_FIRST_START
import com.example.ting.other.dataStore
import com.example.ting.other.toast
import com.example.ting.viewmodel.TingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val navController by lazy { binding.fragmentContainerView.getFragment<NavHostFragment>().navController }

    private val viewModel by viewModels<TingViewModel>()

    private val dataStore by lazy { applicationContext.dataStore }
    private val isFirstStart by lazy {
        dataStore.data.map {
            it[booleanPreferencesKey(KEY_FIRST_START)] ?: true
        }.flowOn(ioDispatcher)
    }

    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            ObjectAnimator.ofFloat(
                splashScreenView.iconView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.view.height.toFloat()
            ).apply {
                interpolator = AnticipateInterpolator()
                duration = 500L
                doOnEnd {
                    splashScreenView.remove()
                }
                lifecycleScope.launch {
                    if (isFirstStart.first()) {
                        getString(R.string.toast_first_start).toast()
                        splashScreenView.view.setOnClickListener {
                            start()
                            lifecycleScope.launch {
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
        splashScreen.setKeepOnScreenCondition { !viewModel.isReady }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.floatingActionButton.isVisible = destination.id != R.id.playerFragment
        }
        binding.floatingActionButton.setOnClickListener {
            navController.navigate(NavigationDirections.actionGlobalPlayerFragment())
        }
    }
}