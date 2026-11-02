/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.geq.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource

@Composable
fun ConfirmationDialog(text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    var showDialog by remember { mutableStateOf(true) }
    if (!showDialog) {
        onDismiss()
        return
    }

    DolbyDialog(
        onDismissRequest = { showDialog = false },
        title = text,
        confirmText = stringResource(id = android.R.string.ok),
        onConfirm = {
            showDialog = false
            onConfirm()
        },
    )
}
