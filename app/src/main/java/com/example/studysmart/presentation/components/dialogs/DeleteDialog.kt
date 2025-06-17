package com.example.studysmart.presentation.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable


@Composable
fun DeletetDialog(
    isOpen: Boolean,
    title: String,
    bodyText: String,
    onDismissRequest: ()-> Unit,
    onConfirmButtonClick: ()-> Unit
) {

    if (isOpen) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = {
                Text(text = title)
            },
            text = {
              Text(text = bodyText)
            },
            confirmButton = {
                TextButton(
                    onClick = { onConfirmButtonClick() }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismissRequest() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}