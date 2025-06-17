package com.example.studysmart.presentation.session

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studysmart.presentation.components.ButtonSection
import com.example.studysmart.presentation.components.RelatedToSubjectSection
import com.example.studysmart.presentation.components.StudySessionList
import com.example.studysmart.presentation.components.SubjectListBottomSheet
import com.example.studysmart.presentation.components.TimerSection
import com.example.studysmart.presentation.components.dialogs.DeletetDialog
import com.example.studysmart.presentation.components.top_app_bars.SessionScreenTopAppBar
import com.example.studysmart.subjects
import com.example.studysmart.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studysmart.util.Constants.ACTION_SERVICE_START
import com.example.studysmart.util.Constants.ACTION_SERVICE_STOP
import com.example.studysmart.util.SnackbarEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    state: SessionState,
    onEvent: (SessionEvent) -> Unit,
    onBackButtonClick: () -> Unit,
    timerService: StudySessionTimerService,
    snackBarEvent: SharedFlow<SnackbarEvent>
) {
    var snackbarHostState = remember { SnackbarHostState() }
    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val currentTimerState by timerService.currentTimerState
    val context: Context = LocalContext.current

    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var isAlertDialogOpen by rememberSaveable { mutableStateOf(false) }




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
    DeletetDialog(
        isOpen = isAlertDialogOpen,
        title = "Delete Task",
        bodyText = "Are you sure, you want to delete this session? " +
                "This action can not be undone.",
        onDismissRequest = {
            isAlertDialogOpen = false
        },
        onConfirmButtonClick = {
            onEvent(SessionEvent.DeleteSession)
            isAlertDialogOpen = false

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
            onEvent(SessionEvent.OnRelatedSubjectChange(subject))
        },
        onDismissRequest = { isBottomSheetOpen = false }
    )
    Scaffold(
        snackbarHost = {
            SnackbarHost((snackbarHostState))
        },
        topBar = {
            SessionScreenTopAppBar(
                onBackButtonClick = onBackButtonClick
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                TimerSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds
                )
            }
            item {
                RelatedToSubjectSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    realatedToSubject = state.relatedToSubject ?: "",
                    selectSubjectButtonClick = {
                        isBottomSheetOpen = true
                    }
                )
            }
            item {
                ButtonSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick = {

                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = if (currentTimerState == TimerState.STARTED) ACTION_SERVICE_STOP else ACTION_SERVICE_START

                        )
                    },
                    cancelButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = ACTION_SERVICE_CANCEL

                        )
                    },
                    finishButtonClicK = {
                        val duration = timerService.duration.seconds
                        if(duration>=36 && state.relatedToSubject != null) {
                            ServiceHelper.triggerForegroundService(
                                context,
                                ACTION_SERVICE_STOP
                            )
                            ServiceHelper.triggerForegroundService(
                                context,
                                ACTION_SERVICE_CANCEL
                            )
                        }
                            onEvent(SessionEvent.SaveSession(duration))
                    },
                    timerState = currentTimerState,
                    seconds = seconds
                )
            }
            StudySessionList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study session."
                        + "Start a study session to begin recording your progress.",
                sessions = state.sessions,
                onDeleteIconClick = { session ->
                    isAlertDialogOpen = true
                    onEvent(SessionEvent.OnDeleteSessionButtonClick(session))
                }
            )
        }
    }
}

fun timerTextAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(duration)) { fullHeight -> fullHeight } +
            fadeIn(animationSpec = tween(duration)) togetherWith
            slideOutVertically(animationSpec = tween(duration)) { fullHeight -> -fullHeight } +
            fadeOut(animationSpec = tween(duration))
}