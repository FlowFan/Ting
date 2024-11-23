package com.example.ting.other

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.ting.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(@StringRes val resId: Int, vararg val formatArgs: Any) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *formatArgs)
        }
    }
}

@Composable
fun Test() {
    val fontScale = LocalDensity.current.fontScale
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val widthPixels = displayMetrics.widthPixels
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = widthPixels / 360.0f,
            fontScale = fontScale
        )
    ) {

    }
}

class TestViewModel : ViewModel() {
    private val textChannel = Channel<UiText>()
    val stringFlow = textChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            textChannel.send(UiText.DynamicString("hello"))
            textChannel.send(UiText.StringResource(R.string.app_name))
        }
    }
}

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = TestViewModel()
        lifecycleScope.launch {
            viewModel.stringFlow.collect {
                it.asString(this@TestActivity)
            }
        }
    }
}