/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.preference

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import co.aospa.dolby.R
import kotlin.math.abs

// Preference with a small preview of the current graphic equalizer curve on the right side
class DolbyGeqPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {

    var gains: IntArray? = null
        set(value) {
            if (field.contentEquals(value)) return
            field = value
            notifyChanged()
        }

    init {
        widgetLayoutResource = R.layout.geq_curve_layout
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val curveView = holder.findViewById(R.id.geq_curve) as ImageView
        val current = gains ?: IntArray(0)
        val icon: Drawable? =
            when {
                current.isEmpty() -> null
                // Flat curve: reuse the intelligent EQ "off" icon so both rows look identical.
                current.all { it == 0 } ->
                    AppCompatResources.getDrawable(context, R.drawable.ic_ieq_off)
                else ->
                    GeqCurveDrawable(
                        current,
                        themeColor(),
                        widthPx = dp(ICON_WIDTH_DP),
                        heightPx = dp(ICON_HEIGHT_DP),
                        insetPx = dp(ICON_INSET_DP),
                    )
            }
        curveView.setImageDrawable(icon)
        curveView.alpha = if (isEnabled) 1f else DISABLED_ALPHA
    }

    private fun themeColor(): Int {
        val a = context.obtainStyledAttributes(intArrayOf(android.R.attr.textColorSecondary))
        return try {
            a.getColor(0, FALLBACK_COLOR)
        } finally {
            a.recycle()
        }
    }

    private fun dp(value: Int) = (value * context.resources.displayMetrics.density).toInt()

    private class GeqCurveDrawable(
        private val gains: IntArray,
        color: Int,
        private val widthPx: Int,
        private val heightPx: Int,
        private val insetPx: Int,
    ) : Drawable() {
        private val paint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                style = Paint.Style.FILL
            }
        private val path = Path()

        override fun getIntrinsicWidth() = widthPx

        override fun getIntrinsicHeight() = heightPx

        override fun draw(canvas: Canvas) {
            if (gains.isEmpty()) return
            val b = bounds
            // The artwork is inset horizontally, like the intelligent EQ icons.
            val left = b.left + insetPx.toFloat()
            val right = b.right - insetPx.toFloat()
            val width = right - left
            // Scale to the biggest gain so the shape uses the available height, but never
            // amplify tiny adjustments into something that looks dramatic.
            val scale = maxOf(MIN_SCALE, gains.maxOf { abs(it) }).toFloat()
            val mid = b.top + b.height() * 0.5f
            val amplitude = b.height() * 0.4f
            val last = gains.size - 1

            fun x(i: Int) = left + if (last == 0) 0f else width * i / last
            fun y(i: Int) = mid - gains[i] / scale * amplitude

            path.reset()
            path.moveTo(left, b.bottom.toFloat())
            path.lineTo(x(0), y(0))
            for (i in 1..last) {
                val midX = (x(i - 1) + x(i)) / 2f
                val midY = (y(i - 1) + y(i)) / 2f
                path.quadTo(x(i - 1), y(i - 1), midX, midY)
            }
            path.lineTo(x(last), y(last))
            path.lineTo(right, b.bottom.toFloat())
            path.close()
            canvas.drawPath(path, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        @Deprecated("Deprecated in Java")
        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }

    companion object {
        private const val DISABLED_ALPHA = 0.38f
        private const val FALLBACK_COLOR = 0xFF888888.toInt()
        private const val MIN_SCALE = 60
        private const val ICON_WIDTH_DP = 56
        private const val ICON_HEIGHT_DP = 20
        private const val ICON_INSET_DP = 4
    }
}
