package com.example.studysmart.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studysmart.R
import com.example.studysmart.domain.model.Task
import com.example.studysmart.presentation.components.cards.TaskCard


fun LazyListScope.TasksList(
    sectionTitle: String,
    tasks: List<Task>,
    emptyListText: String,
    onTaskCardClick: (Int)-> Unit,
    onCheckBoxClicK : (Task)-> Unit
) {
    item {

        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }

    if (tasks.isEmpty()) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.img_tasks),
                    contentDescription = emptyListText
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = emptyListText,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
        items(tasks){
            TaskCard(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                task = it,
                onCheckBoxClick = { onCheckBoxClicK(it) },
                onClick = { onTaskCardClick(it.taskId) }
            )
        }


}