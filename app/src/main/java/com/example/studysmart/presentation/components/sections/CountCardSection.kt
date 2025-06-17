package com.example.studysmart.presentation.components.sections

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studysmart.presentation.components.cards.CountCard


@Composable
fun CountCardSection(
    modifier: Modifier = Modifier,
    subjectCount: Int,
    studiedHours: Float,
    goalHours: Float
) {

    Row(
        modifier = modifier
    ){
        CountCard(
            headingText = "Subject Count",
            count = "$subjectCount",
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            headingText = "Studied Hours",
            count = "${String.format("%.2f", studiedHours/3600.0).toFloat()}",
            modifier = Modifier.weight(1f)

        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            headingText = "Goal Study Hours",
            count = "${String.format("%.2f",goalHours).toFloat()}",
            modifier = Modifier.weight(1f)

        )
    }
}