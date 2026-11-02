/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.geq.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Each slider column is 40dp wide but only draws a ~16dp track, leaving 12dp of empty space on
// each side of it.
private val SLIDER_SIDE_INSET = 12.dp

/**
 * Extends the content [inset] into the start padding of the parent, so the first slider track lines
 * up with the left edge of the screen content instead of floating [inset] away from it.
 */
private fun Modifier.overflowStart(inset: Dp): Modifier = layout { measurable, constraints ->
    val insetPx = inset.roundToPx()
    val placeable =
        measurable.measure(
            constraints.copy(
                minWidth = constraints.minWidth + insetPx,
                maxWidth = constraints.maxWidth + insetPx,
            )
        )
    layout((placeable.width - insetPx).coerceAtLeast(0), placeable.height) {
        placeable.place(-insetPx, 0)
    }
}

@Composable
fun EqualizerBands(viewModel: EqualizerViewModel) {
    val preset by viewModel.preset.collectAsState()
    val bandGains = preset.bandGains

    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.overflowStart(SLIDER_SIDE_INSET).fillMaxWidth(),
    ) {
        items(bandGains.size) { index ->
            BandGainSlider(
                bandGains[index],
                onValueChangeFinished = { viewModel.setGain(index, it) },
            )
        }
    }
}
