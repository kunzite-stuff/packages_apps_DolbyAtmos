/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import co.aospa.dolby.R

// Preference whose icon sits at the same left edge as the other items' titles
class DolbyResetPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {

    init {
        layoutResource = R.layout.dolby_reset_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val iconView = holder.findViewById(android.R.id.icon) as? ImageView ?: return
        val tinted = icon?.mutate() ?: return
        tinted.setTint(iconColor())
        iconView.setImageDrawable(tinted)
        iconView.alpha = if (isEnabled) 1f else DISABLED_ALPHA
    }

    private fun iconColor(): Int {
        val a = context.obtainStyledAttributes(intArrayOf(android.R.attr.textColorSecondary))
        return try {
            a.getColor(0, FALLBACK_COLOR)
        } finally {
            a.recycle()
        }
    }

    companion object {
        private const val DISABLED_ALPHA = 0.38f
        private const val FALLBACK_COLOR = 0xFF888888.toInt()
    }
}
