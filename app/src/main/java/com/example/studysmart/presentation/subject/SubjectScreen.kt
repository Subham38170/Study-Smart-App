package com.example.studysmart.presentation.subject

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.studysmart.presentation.components.StudySessionList
import com.example.studysmart.presentation.components.SubjectOverviewSection
import com.example.studysmart.presentation.components.TasksList
import com.example.studysmart.presentation.components.dialogs.AddSubjectDialog
import com.example.studysmart.presentation.components.dialogs.DeletetDialog
import com.example.studysmart.presentation.components.top_app_bars.SubjectScreenTopAppBar
import com.example.studysmart.util.SnackbarEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    state: SubjectState,
    onEvent: (SubjectEvent) -> Unit,
    onBackButtonClick: () -> Unit,
    onAddTaskButtonClick: () -> Unit,
    onTaskCardClick: (Int, Int) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>
) {



    val listState = rememberLazyListState()
    val isFABExpanded by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var isDeleteSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isEditSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }


    var snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest { event ->
            when (event) {
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                is SnackbarEvent.NavigateUp -> {
                    onBackButtonClick()
                }
            }
        }
    }
    LaunchedEffect(key1 = state.studiedHours, key2 = state.goalStudyHours) {
        onEvent(SubjectEvent.UpdateProgress)
    }
    AddSubjectDialog(
        isOpen = isEditSubjectDialogOpen,
        onDismissRequest = {
            isEditSubjectDialogOpen = false
        },
        onConfirmButton = {
            isEditSubjectDialogOpen = false
            onEvent(SubjectEvent.UpdateSubject)
        },
        selectedColors = state.subjectCardColors,
        onColorChange = { onEvent(SubjectEvent.OnSubjectCardColorChange(it)) },
        onSubjectNameChange = { onEvent(SubjectEvent.OnSubjectNameChange(it)) },
        onGoalHoursChange = { onEvent(SubjectEvent.OnGoalStudyHoursChange(it)) },
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours.toString()
    )
    DeletetDialog(
        isOpen = isDeleteSubjectDialogOpen,
        title = "Delete Subject?",
        bodyText = "Are you sure, you want to delete this subject? All related "
                + "tasks and study sessions will be permanently,",
        onDismissRequest = { isDeleteSubjectDialogOpen = false },
        onConfirmButtonClick = {
            isDeleteSubjectDialogOpen = false
            onEvent(SubjectEvent.DeleteSubject)


        }
    )
    DeletetDialog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced"
                + "by this session time. This action can not be undo",
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmButtonClick = {
            isDeleteSessionDialogOpen = false
            onEvent(SubjectEvent.DeleteSubject)
        }
    )
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopAppBar(
                scrollBehavior = scrollBehavior,
                title = state.subjectName,
                onBackButtonClicK = onBackButtonClick,
                onDeleteButtonClick = { isDeleteSubjectDialogOpen = true },
                onEdituttonClick = { isEditSubjectDialogOpen = true }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskButtonClick,
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Task") },
                expanded = isFABExpanded
            )
        }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                SubjectOverviewSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    studiedHours = state.studiedHours,
                    goalHours = state.goalStudyHours,
                    progress = state.progress
                )
            }
            TasksList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks.\n"
                        + "Click the + button in subject screen to add",
                tasks = state.upcomingTasks,
                onCheckBoxClicK = { onEvent(SubjectEvent.OnTaskIsCompleteChange(it)) },
                onTaskCardClick = {
                    onTaskCardClick(it, state.currentSubjectId!!)
                }
            )
            item { Spacer(modifier = Modifier.height(20.dp)) }
            TasksList(
                sectionTitle = "COMPLETED TASKS",
                emptyListText = "You don't have any completed tasks.\n"
                        + "Click the check box on completion of task.",
                tasks = state.completedTasks,
                onCheckBoxClicK = { onEvent(SubjectEvent.OnTaskIsCompleteChange(it)) },
                onTaskCardClick = {
                    onTaskCardClick(it, state.currentSubjectId!!)

                }
            )
            item { Spacer(modifier = Modifier.height(20.dp)) }

            StudySessionList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study session."
                        + "Start a study session to begin recording your progress.",
                sessions = state.recentSessions,
                onDeleteIconClick = {
                    isDeleteSessionDialogOpen = true
                    onEvent(SubjectEvent.OnDeleteSessionButton(it))
                }
            )
        }


    }

}