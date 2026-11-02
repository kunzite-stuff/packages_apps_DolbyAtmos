/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.preference

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.preference.ListPreference
import co.aospa.dolby.R

/**
 * A [ListPreference] that opens a list dialog with the selected row highlighted and a check, instead
 * of the default radio buttons. Entries, values, persistence and the change listener behave like a
 * regular ListPreference. Subclasses can show an icon per row through [getRowIcon].
 */
open class DolbyListPreference(context: Context, attrs: AttributeSet?) :
    ListPreference(context, attrs) {

    /** Icon for the row at [index], colored with [color]. Null shows a text-only row. */
    protected open fun getRowIcon(index: Int, value: String, color: Int): Drawable? = null

    override fun onClick() {
        showChoiceDialog()
    }

    private fun showChoiceDialog() {
        val labels = entries ?: return
        val values = entryValues ?: return
        val accent = themeColor(android.R.attr.colorAccent)
        val textColor = themeColor(android.R.attr.textColorPrimary)

        val adapter =
            object : BaseAdapter() {
                override fun getCount() = labels.size

                override fun getItem(position: Int): Any = labels[position]

                override fun getItemId(position: Int) = position.toLong()

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val row =
                        convertView
                            ?: LayoutInflater.from(context)
                                .inflate(R.layout.dolby_profile_dialog_item, parent, false)
                    val itemValue = values[position].toString()
                    val selected = itemValue == value

                    row.findViewById<LinearLayout>(R.id.profile_row).background =
                        if (selected) {
                            GradientDrawable().apply {
                                cornerRadius = dp(12).toFloat()
                                setColor(withAlpha(accent, 0x33))
                            }
                        } else {
                            null
                        }
                    row.findViewById<ImageView>(R.id.profile_row_icon).apply {
                        val rowIcon =
                            getRowIcon(position, itemValue, if (selected) accent else textColor)
                        setImageDrawable(rowIcon)
                        visibility = if (rowIcon != null) View.VISIBLE else View.GONE
                    }
                    row.findViewById<TextView>(R.id.profile_row_name).apply {
                        text = labels[position]
                        setTextColor(if (selected) accent else textColor)
                    }
                    row.findViewById<ImageView>(R.id.profile_row_check).apply {
                        visibility = if (selected) View.VISIBLE else View.GONE
                        setImageDrawable(tinted(R.drawable.ic_check, accent))
                    }
                    return row
                }
            }

        val dialog =
            AlertDialog.Builder(context)
                .setTitle(dialogTitle ?: title)
                .setAdapter(adapter) { dialogInterface, which ->
                    val newValue = values[which].toString()
                    if (newValue != value && callChangeListener(newValue)) value = newValue
                    dialogInterface.dismiss()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        dialog.show()
        dialog.listView?.apply {
            divider = null
            dividerHeight = 0
        }
    }

    protected fun tinted(resId: Int, color: Int): Drawable? =
        AppCompatResources.getDrawable(context, resId)?.mutate()?.also { it.setTint(color) }

    protected fun themeColor(attr: Int): Int {
        val a = context.obtainStyledAttributes(intArrayOf(attr))
        return try {
            a.getColor(0, FALLBACK_COLOR)
        } finally {
            a.recycle()
        }
    }

    protected fun withAlpha(color: Int, alpha: Int) = (color and 0x00FFFFFF) or (alpha shl 24)

    protected fun dp(value: Int) = (value * context.resources.displayMetrics.density).toInt()

    companion object {
        private const val FALLBACK_COLOR = 0xFF888888.toInt()
    }
}
