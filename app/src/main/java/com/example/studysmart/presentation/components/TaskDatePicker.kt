package com.example.studysmart.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.core.graphics.isWideGamut
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(
    state: DatePickerState,
    isOpen: Boolean,
    confirmButtonText: String = "OK",
    dismissButtonText: String = "Cancel",
    onDismissRequest: ()-> Unit,
    onConfirmButtonClicked: ()-> Unit
){

    if(isOpen) {
        DatePickerDialog(
            onDismissRequest = { onDismissRequest() },
            confirmButton = {
                val currentDate = LocalDate.now(ZoneId.systemDefault())
                val selectedDate = state.selectedDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                TextButton(
                    onClick = onConfirmButtonClicked,
                    enabled =  selectedDate!! >= currentDate
                ) {
                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {

                TextButton(
                    onClick = { onDismissRequest }

                ) {
                    Text(
                        text = dismissButtonText
                    )
                }
            },
            content = {
               DatePicker(
                   state = state
               )
            }
        )
    }
}