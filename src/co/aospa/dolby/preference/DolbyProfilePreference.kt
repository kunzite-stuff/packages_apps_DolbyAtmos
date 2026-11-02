/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.preference

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.preference.PreferenceViewHolder
import co.aospa.dolby.R

/**
 * Profile chooser shown as a card with the active profile (icon and name). Tapping it opens the
 * list dialog from [DolbyListPreference], with an icon and a name per profile. Values come from
 * R.array.dolby_profile_values.
 */
class DolbyProfilePreference(context: Context, attrs: AttributeSet?) :
    DolbyListPreference(context, attrs) {

    init {
        layoutResource = R.layout.dolby_profile_card
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val accent = themeColor(android.R.attr.colorAccent)
        val secondary = themeColor(android.R.attr.textColorSecondary)
        val dim = if (isEnabled) 1f else DISABLED_ALPHA

        (holder.findViewById(R.id.profile_icon_tile) as? View)?.apply {
            background =
                GradientDrawable().apply {
                    cornerRadius = dp(12).toFloat()
                    setColor(withAlpha(accent, 0x33))
                }
            alpha = dim
        }
        (holder.findViewById(R.id.profile_icon) as? ImageView)?.setImageDrawable(
            tinted(iconRes(value), accent)
        )
        (holder.findViewById(R.id.profile_chevron) as? ImageView)?.apply {
            setImageDrawable(tinted(R.drawable.ic_chevron_down, secondary))
            alpha = dim
        }
    }

    override fun getRowIcon(index: Int, value: String, color: Int): Drawable? =
        tinted(iconRes(value), color)

    private fun iconRes(profileValue: String): Int =
        when (profileValue) {
            "0" -> R.drawable.ic_profile_dynamic
            "1" -> R.drawable.ic_profile_video
            "2" -> R.drawable.ic_profile_music
            "8" -> R.drawable.ic_profile_voice
            else -> R.drawable.ic_profile_custom
        }

    companion object {
        private const val DISABLED_ALPHA = 0.38f
    }
}
