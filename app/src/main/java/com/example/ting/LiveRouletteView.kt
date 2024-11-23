package com.example.ting

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.withRotation
import androidx.core.graphics.withSave
import com.example.ting.other.dp
import com.example.ting.other.sp
import kotlin.math.*

/**
 * 直播间动画转盘
 *
 * @author fanshun
 * @date 2024/11/12 17:50
 */
class LiveRoomRouletteView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val arcColor1 = Color.parseColor("#FCF5FF")
    private val arcColor2 = Color.parseColor("#F1E1FF")
    private val arcColor3 = Color.parseColor("#E8CFFF")
    private val resultArcColor = Color.parseColor("#FFF6B5")
    private val textColor = Color.parseColor("#5D48C4")
    private val resultTextColor = Color.parseColor("#F252FF")
    private val decorateColor = Color.parseColor("#7A49E9")

    private var decorateStrokeWidth = 3.dp.toFloat()
    private var textWidth = 12.dp.toFloat()
    private var textHeight = 68.dp.toFloat()
    private var textTop = 8.dp.toFloat()
    private var textSpace = 1.dp.toFloat()

    private val bounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 2.dp.toFloat()
    }
    private val textBounds = Rect()
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = 12.sp
    }
    private val decoratePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = decorateColor
        strokeWidth = decorateStrokeWidth
    }

    private var scale: Float = 1f
        set(value) {
            field = value
            decorateStrokeWidth *= value
            textWidth *= value
            textHeight *= value
            textTop *= value
            textSpace *= value
            borderPaint.strokeWidth *= value
            textPaint.textSize *= value
        }

    /**
     * 动画最小旋转角度
     */
    private val minRotation = 360f
    private var resultIndex: Int? = null
    private var resultPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.TRANSPARENT
    }
    private var resultAnim: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        interpolator = LinearInterpolator()
        duration = 160
        repeatCount = 5
        repeatMode = ValueAnimator.REVERSE
        addUpdateListener {
            val newColor = if (it.animatedFraction < 0.5f) {
                resultArcColor
            } else {
                Color.TRANSPARENT
            }
            if (resultPaint.color != newColor) {
                resultPaint.color = newColor
                postInvalidate()
            }
        }
    }

    private val rouletteList = mutableListOf<LiveRoomRouletteItem>()

    var isTurning: Boolean = false
        private set
    var onTextChange: ((String) -> Unit)? = null
    var onAnimationEnd: (() -> Unit)? = null
    var onResult: ((Int?) -> Unit)? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scale = min(w, h).toFloat() / 270.dp
        (min(w, h) / 2f - decorateStrokeWidth).let {
            bounds[-it, -it, it] = it
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(width / 2f, height / 2f)
//        textView.measure(
//            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
//            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
//        )
//        // 设置TextView在Canvas上的位置
//        textView.layout(-800, -20, 20, 20)
//        textView.draw(canvas)
        val radius = min(width, height) / 2f
        var startAngle = 0f
        rouletteList.forEachIndexed { index, liveRoomRouletteItem ->
            paint.color = if (index % 2 == 0) {
                arcColor1
            } else if (index == rouletteList.lastIndex) {
                arcColor3
            } else {
                arcColor2
            }
            val sweepAngle = 360f * liveRoomRouletteItem.weight / rouletteList.sumOf { it.weight }
            canvas.withRotation(startAngle + sweepAngle / 2) {
                canvas.drawArc(bounds, -90 - sweepAngle / 2, sweepAngle, true, paint)
                if (resultIndex == index) {
                    canvas.drawArc(bounds, -90 - sweepAngle / 2, sweepAngle, true, resultPaint)
                }
                canvas.drawArc(bounds, -90 - sweepAngle / 2, sweepAngle, true, borderPaint)
            }
            startAngle += sweepAngle
        }
        startAngle = 0f
        rouletteList.forEachIndexed { index, liveRoomRouletteItem ->
            textPaint.color = if (resultIndex == index) {
                resultTextColor
            } else {
                textColor
            }
            val sweepAngle = 360f * liveRoomRouletteItem.weight / rouletteList.sumOf { it.weight }
            val textTop = sweepAngle * textTop / 36
            canvas.withSave {
                canvas.rotate(startAngle + sweepAngle / 2)
                val minWidth = max(textWidth.toDouble(), (radius) * sin(Math.toRadians(sweepAngle / 2.0)) * 2 * 32 / 54)
                println("minWidth: ${textWidth.toDouble()} ")
                println("minWidth2: ${(radius - -decorateStrokeWidth) * sin(Math.toRadians(sweepAngle / 2.0)) * 2 * 32 / 54}")
                val maxWidth = sqrt((radius - decorateStrokeWidth).toDouble().pow(2) - (radius - textTop - decorateStrokeWidth).toDouble().pow(2)) * 2
                val textWidth = if (sweepAngle >= 180) {
                    maxWidth
                } else {
                    min(maxWidth, minWidth)
                }.toInt()
                println("textWidth: $textWidth")
                val staticLayout = StaticLayout(liveRoomRouletteItem.text, textPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1f, textSpace, true)

//                canvas.clipRect(
//                    textBounds.apply {
//                        set(-textWidth / 2, (-radius + textTop + decorateStrokeWidth).toInt(), textWidth / 2, (-radius + textTop + decorateStrokeWidth + textHeight).toInt())
//                    }
//                )
                canvas.translate(-textWidth / 2f, -radius + textTop + decorateStrokeWidth)
//                canvas.drawColor(Color.RED)
                staticLayout.draw(canvas)

                layout.measure(
                    MeasureSpec.makeMeasureSpec(textWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(textHeight.toInt(), MeasureSpec.EXACTLY)
                )
                textView.layout(-textWidth / 2, (-radius + textTop + decorateStrokeWidth).toInt(), textWidth / 2, 0)
                textView.draw(canvas)
            }
            startAngle += sweepAngle
        }
        canvas.drawCircle(0f, 0f, radius - decorateStrokeWidth, decoratePaint)
    }

    val textView = TextView(context).apply {
        text = "56456as4f4656456as4f4656456as4f4656456as4f46"
        ellipsize = TextUtils.TruncateAt.END
        textSize = 20f
        TextUtils.ellipsize("12421421421421",textPaint, 100f, TextUtils.TruncateAt.END)
        setBackgroundColor(Color.WHITE)
        setTextColor(Color.BLACK)
    }

    val layout = LinearLayout(context).apply {
        addView(textView)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setResult(resultIndex)
    }

    /**
     * 设置转盘数据
     */
    fun setRouletteList(list: List<LiveRoomRouletteItem>) {
        rouletteList.clear()
        rouletteList.addAll(list)
        setResult()
    }

    /**
     * 开始转动
     *
     * @param endIndex 结束位置
     */
    fun startAnim(endIndex: Int) {
        setResult()
        postInvalidate()
        animate().apply {
            interpolator = DecelerateInterpolator()
            duration = 2000
            rotation(minRotation + getEndRotation(endIndex))
            setUpdateListener {
                onTextChange?.invoke(getCurrentText(rotation))
            }
            withStartAction {
                isTurning = true
            }
            withEndAction {
                isTurning = false
                resultIndex = endIndex
                resultAnim.start()
                onAnimationEnd?.invoke()
            }
            start()
        }
    }

    /**
     * 停止转动
     *
     * @param endIndex 结束位置
     */
    fun stopAnim(endIndex: Int) {
        setResult(endIndex)
        postInvalidate()
    }

    private fun setResult(endIndex: Int? = null) {
        isTurning = false
        animate().cancel()
        resultAnim.end()
        if (endIndex == null) {
            resultPaint.color = Color.TRANSPARENT
        }
        onResult?.invoke(endIndex)
        rotation = getEndRotation(endIndex)
        resultIndex = endIndex
    }

    private fun getEndRotation(endIndex: Int?) =
        if (endIndex != null && endIndex in rouletteList.indices) {
            360f - 360f * (rouletteList.subList(0, endIndex).sumOf { it.weight } + rouletteList[endIndex].weight / 2f) / rouletteList.sumOf { it.weight }
        } else {
            0f
        }

    private fun getCurrentText(rotation: Float): String {
        val currentAngle = 360f - rotation % 360f
        var startAngle = 0f
        rouletteList.forEach { item ->
            val sweepAngle = 360f * item.weight / rouletteList.sumOf { it.weight }
            if (currentAngle in startAngle..(startAngle + sweepAngle)) {
                return item.text
            }
            startAngle += sweepAngle
        }
        return ""
    }
}

data class LiveRoomRouletteItem(
    val id: Long = 0,
    val weight: Int = 0,
    val text: String = ""
)