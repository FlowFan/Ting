package com.example.ting.other

import android.content.Context
import androidx.annotation.StringRes

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
