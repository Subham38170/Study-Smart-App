package com.example.studysmart.presentation.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studysmart.presentation.ui.theme.Typography



@Composable
fun CountCard(
    modifier: Modifier = Modifier,
    headingText: String,
    count: String

){

    ElevatedCard(modifier = modifier){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = headingText,
                style = Typography.labelSmall
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = count,
                style = Typography.bodySmall.copy(fontSize = 30.sp)
            )
        }

    }
}

