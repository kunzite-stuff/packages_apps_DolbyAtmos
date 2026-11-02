/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.geq.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.aospa.dolby.R

// IconButton is 48dp wide and draws a 24dp icon, leaving 12dp of empty touch padding on each side.
private val ICON_BUTTON_INSET = 12.dp

/**
 * Reports a narrower width than the content really has, so the content overflows into the parent's
 * end padding. Used to line the last icon up with the right edge of the screen content instead of
 * leaving it [inset] short of it.
 */
private fun Modifier.overflowEnd(inset: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val reportedWidth = (placeable.width - inset.roundToPx()).coerceAtLeast(0)
    layout(reportedWidth, placeable.height) { placeable.place(0, 0) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetSelector(viewModel: EqualizerViewModel) {
    val presets by viewModel.presets.collectAsState()
    val currentPreset by viewModel.preset.collectAsState()
    var showPresetPicker by remember { mutableStateOf(false) }
    var showNewPresetDialog by remember { mutableStateOf(false) }
    var showRenamePresetDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PresetField(
            name = currentPreset.name,
            onClick = { showPresetPicker = true },
            modifier = Modifier.padding(end = 8.dp).weight(1f),
        )

        Row(
            modifier = Modifier.overflowEnd(ICON_BUTTON_INSET),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TooltipIconButton(
                icon = ImageVector.vectorResource(id = R.drawable.save_as_24px),
                text = stringResource(id = R.string.dolby_geq_new_preset),
                onClick = { showNewPresetDialog = true },
            )

            if (currentPreset.isUserDefined) {
                TooltipIconButton(
                    icon = Icons.Default.Edit,
                    text = stringResource(id = R.string.dolby_geq_rename_preset),
                    onClick = { showRenamePresetDialog = true },
                )
                TooltipIconButton(
                    icon = Icons.Default.Delete,
                    text = stringResource(id = R.string.dolby_geq_delete_preset),
                    onClick = { showDeleteConfirmDialog = true },
                )
            }

            TooltipIconButton(
                icon = ImageVector.vectorResource(id = R.drawable.reset_settings_24px),
                text = stringResource(id = R.string.dolby_geq_reset_gains),
                onClick = {
                    if (currentPreset.isUserDefined) {
                        showResetConfirmDialog = true
                    } else {
                        viewModel.reset()
                    }
                },
            )
        }
    }

    // Dialogs

    if (showPresetPicker) {
        val presetList = presets.toList()
        PresetPickerDialog(
            names = presetList.map { it.name },
            selectedName = currentPreset.name,
            onSelect = { index ->
                viewModel.setPreset(presetList[index])
                showPresetPicker = false
            },
            onDismiss = { showPresetPicker = false },
        )
    }

    if (showNewPresetDialog) {
        PresetNameDialog(
            title = stringResource(id = R.string.dolby_geq_new_preset),
            onPresetNameSet = {
                return@PresetNameDialog viewModel.createNewPreset(name = it)
            },
            onDismissDialog = { showNewPresetDialog = false },
        )
    }

    if (showRenamePresetDialog) {
        PresetNameDialog(
            title = stringResource(id = R.string.dolby_geq_rename_preset),
            presetName = currentPreset.name,
            onPresetNameSet = {
                return@PresetNameDialog viewModel.renamePreset(preset = currentPreset, name = it)
            },
            onDismissDialog = { showRenamePresetDialog = false },
        )
    }

    if (showDeleteConfirmDialog) {
        ConfirmationDialog(
            text = stringResource(id = R.string.dolby_geq_delete_preset_prompt),
            onConfirm = { viewModel.deletePreset(currentPreset) },
            onDismiss = { showDeleteConfirmDialog = false },
        )
    }

    if (showResetConfirmDialog) {
        ConfirmationDialog(
            text = stringResource(id = R.string.dolby_geq_reset_gains_prompt),
            onConfirm = { viewModel.reset() },
            onDismiss = { showResetConfirmDialog = false },
        )
    }
}

/** Rounded card showing the current preset, in the same style as the main settings cards. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetField(name: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.dolby_geq_preset),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_chevron_down),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** List dialog in the same style as the list dialogs of the main settings screen. */
@Composable
private fun PresetPickerDialog(
    names: List<String>,
    selectedName: String,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    DolbyDialog(
        onDismissRequest = onDismiss,
        title = stringResource(id = R.string.dolby_geq_preset),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier =
                Modifier.weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
        ) {
            names.forEachIndexed { index, name ->
                PresetRow(
                    name = name,
                    selected = name == selectedName,
                    onClick = { onSelect(index) },
                )
            }
        }
    }
}

@Composable
private fun PresetRow(name: String, selected: Boolean, onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected) primary.copy(alpha = 0.2f) else Color.Transparent)
                .clickable(onClick = onClick)
                .heightIn(min = 52.dp)
                .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
