package com.example.ting.other

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ting.init.AppInitializer
import com.example.ting.other.Constants.APP_SECRET
import com.example.ting.other.Constants.SHP_DATASTORE
import com.ximalaya.ting.android.opensdk.httputil.util.BASE64Encoder
import com.ximalaya.ting.android.opensdk.httputil.util.HMACSHA1
import com.ximalaya.ting.android.player.MD5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(SHP_DATASTORE)

public inline fun <T, R> Iterable<T>.zipWithNextLast(transform: (a: T, b: T) -> R): List<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()
    val result = mutableListOf<R>()
    var current = iterator.next()
    while (iterator.hasNext()) {
        val next = iterator.next()
        result.add(transform(current, next))
        current = next
    }
    result.add(transform(current, current))
    return result
}

fun String.toast() = Toast.makeText(AppInitializer.mContext, this, Toast.LENGTH_SHORT).show()

fun String.sig(): String = MD5.md5(HMACSHA1.HmacSHA1Encrypt(BASE64Encoder.encode(this), APP_SECRET))

fun Map<String, Any>.sig() = toSortedMap().entries.joinToString("&").sig()

fun Context.isConnectedNetwork() = getSystemService<ConnectivityManager>()?.let {
    it.getNetworkCapabilities(it.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false
} ?: false

fun Number.convertNumber(): String = when {
    this.toLong() < 10000 -> toString()
    this.toLong() < 100000000 -> String.format("%.1f万", toDouble() / 10000)
    else -> DecimalFormat("0.#亿").format(toDouble() / 100000000)
}

fun @receiver:StringRes Int.string(vararg formatArgs: Any?) = AppInitializer.mContext.getString(this, *formatArgs)

fun <T> LifecycleOwner.collectWhenStarted(
    flow: Flow<T>,
    collector: FlowCollector<T>
) {
    lifecycleScope.launch {
        flow.flowWithLifecycle(lifecycle).collect(collector)
    }
}

fun <T> LifecycleOwner.collectLatestWhenStarted(
    flow: Flow<T>,
    action: suspend (value: T) -> Unit
) {
    lifecycleScope.launch {
        flow.flowWithLifecycle(lifecycle).collectLatest(action)
    }
}

fun LifecycleOwner.launchWithLifecycle(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launch(context, start) {
    repeatOnLifecycle(minActiveState, block)
}

context(lifecycleOwner: LifecycleOwner)
fun <T> Flow<T>.collectWithLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>
) {
    lifecycleOwner.lifecycleScope.launch {
        flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect(collector)
    }
}

context(lifecycleOwner: LifecycleOwner)
fun <T> Flow<T>.collectLatestWithLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (value: T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collectLatest(action)
    }
}

class TextChangedListener {
    private var beforeTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null
    private var onTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null
    private var afterTextChanged: ((Editable?) -> Unit)? = null

    fun beforeTextChanged(block: (text: CharSequence?, start: Int, count: Int, after: Int) -> Unit) {
        beforeTextChanged = block
    }

    fun onTextChanged(block: (text: CharSequence?, start: Int, count: Int, after: Int) -> Unit) {
        onTextChanged = block
    }

    fun afterTextChanged(block: (text: Editable?) -> Unit) {
        afterTextChanged = block
    }

    fun build() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged?.invoke(s, start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            afterTextChanged?.invoke(s)
        }
    }
}

inline fun EditText.addTextChangedListener(listener: TextChangedListener.() -> Unit): TextWatcher {
    val textChangedListener = TextChangedListener().apply(listener).build()
    addTextChangedListener(textChangedListener)
    return textChangedListener
}

inline fun RecyclerView.setOnItemClickListener(
    crossinline listener: (Int, RecyclerView.ViewHolder) -> Unit
) {
    addOnItemTouchListener(
        object : RecyclerView.OnItemTouchListener {
            val gestureDetector = GestureDetector(
                context,
                object : GestureDetector.OnGestureListener {
                    override fun onDown(e: MotionEvent): Boolean = false

                    override fun onShowPress(e: MotionEvent) {
                    }

                    override fun onSingleTapUp(e: MotionEvent): Boolean {
                        findChildViewUnder(e.x, e.y)?.let {
                            listener(getChildAdapterPosition(it), getChildViewHolder(it))
                        }
                        return false
                    }

                    override fun onScroll(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        distanceX: Float,
                        distanceY: Float
                    ): Boolean = false

                    override fun onLongPress(e: MotionEvent) {
                    }

                    override fun onFling(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        velocityX: Float,
                        velocityY: Float
                    ): Boolean = false
                }
            )

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(e)
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        }
    )
}