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
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.preference.PreferenceViewHolder
import co.aospa.dolby.R

/**
 * Chooser for a setting with ordered levels (dialogue enhancer, stereo widening). Opens the list
 * dialog from [DolbyListPreference] and shows a small level icon on the right side, in the same
 * place as the equalizer icons: entry N lights up N + 1 of the sticks, one stick per entry.
 */
class DolbyLevelPreference(context: Context, attrs: AttributeSet?) :
    DolbyListPreference(context, attrs) {

    init {
        widgetLayoutResource = R.layout.ieq_icon_layout
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val iconView = holder.findViewById(R.id.ieq_icon) as? ImageView ?: return
        iconView.setImageDrawable(
            LevelSticksDrawable(
                lit = findIndexOfValue(value) + 1,
                total = entries?.size ?: 0,
                color = themeColor(android.R.attr.textColorSecondary),
                widthPx = dp(ICON_WIDTH_DP),
                heightPx = dp(ICON_HEIGHT_DP),
                artWidthPx = dp(ART_WIDTH_DP),
                endInsetPx = dp(ICON_END_INSET_DP),
            )
        )
        iconView.alpha = if (isEnabled) 1f else DISABLED_ALPHA
    }

    private class LevelSticksDrawable(
        private val lit: Int,
        private val total: Int,
        private val color: Int,
        private val widthPx: Int,
        private val heightPx: Int,
        private val artWidthPx: Int,
        private val endInsetPx: Int,
    ) : Drawable() {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val rect = RectF()
        private var alphaMultiplier = 255

        override fun getIntrinsicWidth() = widthPx

        override fun getIntrinsicHeight() = heightPx

        override fun draw(canvas: Canvas) {
            if (total <= 0) return
            val b = bounds
            // Right-aligned inside the icon box, like the intelligent EQ icons.
            val right = b.right - endInsetPx.toFloat()
            val left = right - artWidthPx
            val barHeight = b.height() / (total + (total - 1) * GAP_RATIO)
            val gap = barHeight * GAP_RATIO
            for (i in 0 until total) {
                val bottom = b.bottom - i * (barHeight + gap)
                rect.set(left, bottom - barHeight, right, bottom)
                val alpha = (if (i < lit) 0xFF else DIM_ALPHA) * alphaMultiplier / 255
                paint.color = (color and 0x00FFFFFF) or (alpha shl 24)
                canvas.drawRoundRect(rect, barHeight / 2f, barHeight / 2f, paint)
            }
        }

        override fun setAlpha(alpha: Int) {
            alphaMultiplier = alpha
            invalidateSelf()
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        @Deprecated("Deprecated in Java")
        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }

    companion object {
        private const val ICON_WIDTH_DP = 56
        private const val ICON_HEIGHT_DP = 20
        private const val ART_WIDTH_DP = 48
        private const val ICON_END_INSET_DP = 4
        private const val GAP_RATIO = 0.6f
        private const val DIM_ALPHA = 0x4D
        private const val DISABLED_ALPHA = 0.38f
    }
}
