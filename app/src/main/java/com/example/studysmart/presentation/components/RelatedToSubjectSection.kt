package com.example.studysmart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.studysmart.util.changeMillisToDateString

@Composable
fun RelatedToSubjectSection(
    modifier: Modifier = Modifier,
    realatedToSubject: String,
    selectSubjectButtonClick: ()-> Unit
) {
    Column(
        modifier = modifier
    ){
        Text(
            text = "Related to subject",
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = realatedToSubject,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(
                onClick = selectSubjectButtonClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select subject"
                )
            }
        }
    }
}