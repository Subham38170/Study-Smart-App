package com.example.studysmart.presentation.components.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studysmart.domain.model.Session
import com.example.studysmart.util.changeMillisToDateString
import com.example.studysmart.util.toHours

@Composable
fun StuddySessionCard(
    modifier: Modifier = Modifier,
    session: Session,
    onDeleteClick: ()-> Unit
){

    Card(modifier = modifier){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(modifier = Modifier.padding(start = 12.dp)){
                Text(
                    text = session.relatedToSubject,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = session.date.changeMillisToDateString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${String.format("%.2f",session.duration/3600.0).toFloat()} hr",
                    style = MaterialTheme.typography.bodySmall
                )
                IconButton(
                    onClick = {onDeleteClick()}
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }


            }


    }

}