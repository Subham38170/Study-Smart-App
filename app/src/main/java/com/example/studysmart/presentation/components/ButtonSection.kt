package com.example.studysmart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studysmart.presentation.session.TimerState
import com.example.studysmart.presentation.ui.theme.Red

@Composable
fun ButtonSection(
    modifier: Modifier = Modifier,
    startButtonClick: ()-> Unit,
    cancelButtonClick: ()-> Unit,
    finishButtonClicK: ()-> Unit,
    timerState: TimerState,
    seconds: String
){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Button(
            onClick =cancelButtonClick,
            enabled = seconds!="00" && timerState!= TimerState.STARTED
        ) {
            Text(
                text = "Cancel",
                modifier = Modifier.padding(10.dp)
            )
        }
        Button(
            onClick =startButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if(timerState== TimerState.STARTED) Red else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = when(timerState){
                  TimerState.STARTED -> "Stop"
                  TimerState.STOPPED -> "Resume"
                  else -> "Start"
                },
                modifier = Modifier.padding(10.dp)
            )
        }
        Button(
            onClick = finishButtonClicK,
            enabled = seconds!="00" && timerState!= TimerState.STARTED

        ) {
            Text(
                text = "Finish",
                modifier = Modifier.padding(10.dp)
            )
        }

    }

}