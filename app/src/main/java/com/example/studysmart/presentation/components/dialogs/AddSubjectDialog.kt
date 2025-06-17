package com.example.studysmart.presentation.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.studysmart.domain.model.Subject
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

@Composable
fun AddSubjectDialog(
    isOpen: Boolean,
    title: String = "Add/Update Subject",
    onDismissRequest: ()-> Unit,
    onConfirmButton: ()-> Unit,
    selectedColors: List<Color>,
    onColorChange: (List<Color>) -> Unit,
    onSubjectNameChange: (String)-> Unit,
    onGoalHoursChange: (String)-> Unit,
    subjectName: String,
    goalHours: String
) {
    var goalHoursError by rememberSaveable { mutableStateOf<String?>(null) }
    var subjectNameError by rememberSaveable { mutableStateOf<String?>(null) }
    subjectNameError = when{
        subjectName.isBlank() -> "Please enter subject name."
        subjectName.length <2 -> "Subject name is too short"
        subjectName.length >20 -> "Subject name is too long"
        else  -> null
    }
    goalHoursError = when{
        goalHours.isBlank() -> "Please enter goal study hours."
        goalHours.toFloatOrNull() == null -> "Invalid number"
        goalHours.toFloat() <1f -> "Please set at least 1hours"
        goalHours.toFloat() >1000f -> "Please set a maximum of 1000 hours."
        else -> null
    }
    if (isOpen) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = {
                Text(text = title)
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Subject.subjectCardColors.forEach {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = if(selectedColors==it) Color.Black else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .background(brush = Brush.verticalGradient(it))
                                    .clickable{
                                        onColorChange(it)
                                    }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = {onSubjectNameChange(it)},
                        singleLine = true,
                        label = {Text(text = "Subject Name")},
                        isError = subjectNameError !=null && subjectName.isNotBlank(),
                        supportingText = { Text(text = subjectNameError.orEmpty())}
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = goalHours,
                        onValueChange = {onGoalHoursChange(it)},
                        singleLine = true,
                        label = {Text(text = "Goal Study Hours")},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = goalHoursError != null && goalHours.isNotBlank(),
                        supportingText = { Text(text = goalHoursError.orEmpty())}
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onConfirmButton() },
                    enabled = subjectNameError == null && goalHoursError == null
                ) {
                    Text(text = "Save")
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