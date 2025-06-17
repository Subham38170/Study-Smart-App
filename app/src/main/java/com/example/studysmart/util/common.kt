package com.example.studysmart.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import com.example.studysmart.presentation.ui.theme.Green
import com.example.studysmart.presentation.ui.theme.Orange
import com.example.studysmart.presentation.ui.theme.Red
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

enum class Priority(
    val title: String,
    val color: Color,
    val value: Int
){
    LOW(title = "Low", color = Green, value = 0),
    MEDIUM(title = "Medium", color = Orange, value = 1),
    HIGH(title = "High", color = Red, value = 2);

    companion object{
        fun fromInt(value: Int) = Priority.entries.firstOrNull{it.value ==  value} ?: MEDIUM
    }
}

fun Long?.changeMillisToDateString(): String{
    val date: LocalDate = this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyy"))
}

fun Long.toHours(): Float{
    val hours = this.toFloat()
    return "%.2f".format(hours).toFloat()
}

sealed class SnackbarEvent{
    data class ShowSnackbar(
        val message: String,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ): SnackbarEvent()
    data object NavigateUp: SnackbarEvent()
}


