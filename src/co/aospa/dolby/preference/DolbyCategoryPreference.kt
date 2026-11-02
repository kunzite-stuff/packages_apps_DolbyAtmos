/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import co.aospa.dolby.R

// Category header aligned with the item titles and colored with the theme accent color
class DolbyCategoryPreference(context: Context, attrs: AttributeSet?) :
    PreferenceCategory(context, attrs) {

    init {
        layoutResource = R.layout.dolby_category
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        (holder.findViewById(android.R.id.title) as? TextView)?.setTextColor(accentColor())
    }

    private fun accentColor(): Int {
        val a = context.obtainStyledAttributes(intArrayOf(android.R.attr.colorAccent))
        return try {
            a.getColor(0, FALLBACK_COLOR)
        } finally {
            a.recycle()
        }
    }

    companion object {
        private const val FALLBACK_COLOR = 0xFF888888.toInt()
    }
}
