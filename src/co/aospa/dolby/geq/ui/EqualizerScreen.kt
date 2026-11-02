/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.geq.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.settingslib.spa.framework.theme.SettingsDimension
import com.android.settingslib.spa.framework.theme.settingsBackground

@Composable
fun EqualizerScreen(viewModel: EqualizerViewModel, modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier.fillMaxSize().then(modifier),
        color = MaterialTheme.colorScheme.settingsBackground,
    ) {
        // The padding lives inside the surface so content aligned to the very edge
        // (see PresetSelector) is not clipped by it.
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxHeight().padding(SettingsDimension.itemPadding),
        ) {
            PresetSelector(viewModel = viewModel)
            EqualizerBands(viewModel = viewModel)
        }
    }
}
