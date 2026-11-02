/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.geq.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.aospa.dolby.R

@Composable
fun PresetNameDialog(
    title: String,
    presetName: String = "",
    onPresetNameSet: (String) -> PresetNameValidationError?,
    onDismissDialog: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(true) }
    if (!showDialog) {
        onDismissDialog()
        return
    }
    var text by remember { mutableStateOf(presetName) }
    var error by remember { mutableStateOf<PresetNameValidationError?>(null) }

    DolbyDialog(
        onDismissRequest = { showDialog = false },
        title = title,
        confirmText = stringResource(id = android.R.string.ok),
        onConfirm = {
            val validationError = onPresetNameSet(text)
            if (validationError != null) {
                // validation failed
                error = validationError
            } else {
                // succeeded
                showDialog = false
                error = null
            }
        },
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(id = R.string.dolby_geq_preset_name)) },
                isError = error != null,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            error?.let {
                Text(
                    text = it.toErrorMessage(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
