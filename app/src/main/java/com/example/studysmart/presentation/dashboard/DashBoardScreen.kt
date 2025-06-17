package com.example.studysmart.presentation.dashboard

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.studysmart.domain.model.Session
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.domain.model.Task
import com.example.studysmart.presentation.components.sections.CountCardSection
import com.example.studysmart.presentation.components.dialogs.AddSubjectDialog
import com.example.studysmart.presentation.components.top_app_bars.DashboardScreenTopBar
import com.example.studysmart.presentation.components.dialogs.DeletetDialog
import com.example.studysmart.presentation.components.StudySessionList
import com.example.studysmart.presentation.components.SubjectCardSection
import com.example.studysmart.presentation.components.TasksList
import com.example.studysmart.util.SnackbarEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


@Composable
fun DashBoardScreen(
    state: DashboardState,
    onEvent: (DashboardEvent) -> Unit,
    onSubjectCardClick: (Int?) -> Unit,
    onTaskCardClick: (Int) -> Unit,
    snackBarEvent: SharedFlow<SnackbarEvent>,
    onStartSessionButtonClick: () -> Unit


) {
    var snackbarHostState = remember { SnackbarHostState() }
    var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest { event->
            when(event){
                is SnackbarEvent.ShowSnackbar ->{
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackbarEvent.NavigateUp -> {}
            }
        }
    }

    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        onDismissRequest = {
            isAddSubjectDialogOpen = false
        },
        onConfirmButton = {
            isAddSubjectDialogOpen = false
            onEvent(DashboardEvent.SaveSubject)
        },
        selectedColors = state.subjectCardColors,
        onColorChange = { onEvent(DashboardEvent.OnSubjectCardColorChange(it))  },
        onSubjectNameChange = { onEvent(DashboardEvent.OnSubjectNameChange(it))},
        onGoalHoursChange = { onEvent(DashboardEvent.OnGoalStudyHoursChange(it)) },
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours
    )
    DeletetDialog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced"
                + "by this session time. This action can not be undo",
        onDismissRequest = {isDeleteSessionDialogOpen = false},
        onConfirmButtonClick = {
            isDeleteSessionDialogOpen = false
            onEvent(DashboardEvent.DeleteSession)
        }
    )
    Scaffold(
        snackbarHost = {
            SnackbarHost((snackbarHostState))
        },
        topBar = { DashboardScreenTopBar() }
    ) { innerpadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {
            item {
                CountCardSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudiedHours,
                    goalHours = state.totalGoalStudyHours
                )

            }
            item {
                SubjectCardSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = state.subjects,
                    onAddIconClicked = {
                        isAddSubjectDialogOpen = true
                    },
                    onSubjectCardClick = onSubjectCardClick
                )
            }
            item {
                Button(
                    onClick = onStartSessionButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Start Study Session"
                    )
                }
            }
            TasksList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks.\n"
                        + "Click the + button in subject screen to add",
                tasks = state.tasks,
                onCheckBoxClicK = {
                    onEvent(DashboardEvent.OnTaskCompleteChange(it))
                },
                onTaskCardClick = onTaskCardClick
            )
            item { Spacer(modifier = Modifier.height(20.dp)) }
            StudySessionList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study session."
                        + "Start a study session to begin recording your progress.",
                sessions = state.sessions,
                onDeleteIconClick = {
                    onEvent(DashboardEvent.OnDeleteSessionButtonClick(it))
                    isDeleteSessionDialogOpen = true
                }
            )

        }
    }
}


