package com.example.studysmart.presentation.task

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studysmart.presentation.components.PriorityButton
import com.example.studysmart.presentation.components.SubjectListBottomSheet
import com.example.studysmart.presentation.components.TaskDatePicker
import com.example.studysmart.presentation.components.dialogs.DeletetDialog
import com.example.studysmart.presentation.components.top_app_bars.TaskScreenTopAppBar
import com.example.studysmart.subjects
import com.example.studysmart.util.Priority
import com.example.studysmart.util.SnackbarEvent
import com.example.studysmart.util.changeMillisToDateString
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    snackBarEvent: SharedFlow<SnackbarEvent>,
    onBackButtonClick: () -> Unit
) {

    var snackbarHostState = remember { SnackbarHostState() }


    var taskTitleError by rememberSaveable { mutableStateOf<String?>(null) }
    var isAlertDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val rotatetionAnim by animateFloatAsState(
        targetValue = if (isBottomSheetOpen) 180f else 0f,
        animationSpec = tween(200)
    )
    taskTitleError = when {
        state.title.isBlank() -> "Please enter task title."
        state.title.length < 4 -> "Task title is too short."
        state.title.length > 30 -> "Task title is too long."
        else -> null
    }

    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest { event ->
            when (event) {
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackbarEvent.NavigateUp -> {
                    onBackButtonClick()
                }
            }
        }
    }

    DeletetDialog(
        isOpen = isAlertDialogOpen,
        title = "Delete Task",
        bodyText = "Are you sure, you want to delete this task? " +
                "This action can not be undone.",
        onDismissRequest = {
            isAlertDialogOpen = false
        },
        onConfirmButtonClick = {
            isAlertDialogOpen = false
            onEvent(TaskEvent.DeleteTask)
        }
    )
    TaskDatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = { isDatePickerDialogOpen = false },
        onConfirmButtonClicked = {
            isDatePickerDialogOpen = false
            onEvent(TaskEvent.OnDateChange(datePickerState.selectedDateMillis))
        }
    )
    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjects,
        onSubjectClicked = { subject ->
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
            onEvent(TaskEvent.OnRelatedSubjectSelect(subject))
        },

        onDismissRequest = { isBottomSheetOpen = false }
    )
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaskScreenTopAppBar(
                isTaskExist = state.currentTaskId != null,
                isComplete = state.isTaskComplete,
                checkBoxBorderColor = state.priority.color,
                onBackButtonClick = onBackButtonClick,
                onDeleteButtonClick = { isAlertDialogOpen = true },
                onCheckBoxClick = { onEvent(TaskEvent.OnIsCompleteChange) }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = { onEvent(TaskEvent.OnTitleChange(it)) },
                label = { Text(text = "Title") },
                singleLine = true,
                isError = taskTitleError != null && state.title.isNotBlank(),
                supportingText = {
                    Text(text = taskTitleError.orEmpty())
                }

            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = { onEvent(TaskEvent.OnDescriptionChange(it)) },
                label = { Text(text = "Description") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Due Date",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.dueDate.changeMillisToDateString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = { isDatePickerDialogOpen = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Due Date"
                    )
                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Priority.entries.forEach { priority ->
                    PriorityButton(
                        modifier = Modifier.weight(1f),
                        label = priority.title,
                        backgroundColor = priority.color,
                        borderColor = if (priority == state.priority) Color.White else Color.Transparent,
                        labelColor = if (priority == state.priority) Color.White else Color.White.copy(
                            alpha = 0.7f
                        ),
                        onClick = {
                            onEvent(TaskEvent.OnPriorityChange(priority))
                        }
                    )
                }

            }
            Spacer(modifier = Modifier.height(30.dp))
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
                    text = state.relatedToSubject ?: "Select",
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = { isBottomSheetOpen = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select subject",
                        modifier = Modifier.rotate(rotatetionAnim)
                    )
                }
            }
            Button(
                onClick = {
                    onEvent(TaskEvent.SaveTask)
                },
                enabled = taskTitleError == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    text = "Save"
                )

            }

        }
    }

}