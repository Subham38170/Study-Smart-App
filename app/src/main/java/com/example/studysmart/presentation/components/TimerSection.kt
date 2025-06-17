package com.example.studysmart.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studysmart.presentation.session.timerTextAnimation

@Composable
fun TimerSection(
    modifier: Modifier = Modifier,
    hours: String,
    minutes: String,
    seconds: String
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )
        Row {
            AnimatedContent(
                targetState = "$hours:",
                label = hours,
                transitionSpec = {timerTextAnimation()}

            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(
                targetState = "$minutes:",
                label = minutes,
                transitionSpec = {timerTextAnimation()}

            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(
                targetState = seconds,
                label = seconds,
                transitionSpec = {timerTextAnimation()}


            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
        }

    }

}