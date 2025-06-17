package com.example.studysmart.presentation.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.domain.model.Task
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.SubjectRepository
import com.example.studysmart.domain.repository.TaskRepository
import com.example.studysmart.presentation.navigation.Routes
import com.example.studysmart.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subjectId = savedStateHandle.toRoute<Routes.SubjectSceen>().subjectId
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()


    private val _state = MutableStateFlow(SubjectState())
    val state = _state

    init {
        fetchSubject()
        fetchUpcomingTasks()
        fetchCompletedTasks()
        fetchRecentTasksForSubject()
    }

    fun onEvent(event: SubjectEvent) {
        when (event) {
            SubjectEvent.DeleteSession -> {
                deleteSession()
            }

            SubjectEvent.DeleteSubject -> {
                deleteSubject()
            }

            is SubjectEvent.OnDeleteSessionButton -> {
                _state.update {
                    it.copy(
                        session = event.session
                    )
                }
            }

            is SubjectEvent.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(
                        goalStudyHours = event.hours
                    )
                }
            }

            is SubjectEvent.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(
                        subjectCardColors = event.color
                    )
                }
            }

            is SubjectEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(
                        subjectName = event.name
                    )
                }
            }

            is SubjectEvent.OnTaskIsCompleteChange -> {
                updateTask(event.task)
            }

            is SubjectEvent.UpdateSubject -> {
                updateSubject()
            }

            is SubjectEvent.UpdateProgress -> {
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f
                val studiedHours = state.value.studiedHours.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(
                        progress = (studiedHours / goalStudyHours).coerceIn(0f, 1f)
                    )
                }
            }


        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Session deleted successfully"
                    )
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't delete session. ${e.message}"
                    )
                )
            }
        }
    }

    private fun updateSubject() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloat(),
                        colors = state.value.subjectCardColors.map { it.toArgb() },
                        subjectId = state.value.currentSubjectId ?: 0
                    )
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Subject updated Sucessfully"
                    )
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Coudn't update subject. ${e.message}",
                        SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun fetchSubject() {
        viewModelScope.launch(Dispatchers.IO) {
            subjectRepository.getSubjectById(subjectId)?.let { subject ->
                _state.update {
                    it.copy(
                        subjectName = subject.name,
                        goalStudyHours = subject.goalHours.toString(),
                        subjectCardColors = subject.colors.map { Color(it) },
                        currentSubjectId = subject.subjectId
                    )
                }
            }
            sessionRepository.getTotalSessionDurationBySubjectId(subjectId).let {
                it.collectLatest { duration ->
                    _state.update {
                        it.copy(
                            studiedHours = "${
                                String.format("%.2f", duration.toFloat() / 3600.0).toFloat()
                            }"
                        )
                    }
                }
            }

        }
    }

    private fun fetchUpcomingTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isUpcomingtaskLoading = true) }
            taskRepository.getUpcomingTasksForSubject(subjectId).collect { tasks ->
                _state.update {
                    it.copy(
                        upcomingTasks = tasks,
                        isUpcomingtaskLoading = false
                    )
                }
            }
        }
    }

    private fun fetchCompletedTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.getCompletedTasksForSubject(subjectId).collectLatest { tasks ->
                _state.update {
                    it.copy(completedTasks = tasks)
                }
            }
        }.invokeOnCompletion {
            _state.update {
                it.copy(
                    isCompletedtaskLoading = true
                )
            }
        }
    }

    private fun fetchRecentTasksForSubject() {
        viewModelScope.launch {
            sessionRepository.getRecentSessionForSubject(subjectId).collectLatest { sessions ->
                _state.update {
                    it.copy(
                        recentSessions = sessions
                    )
                }
            }
        }
    }


    private fun deleteSubject() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentSubjectId = state.value.currentSubjectId
                if (currentSubjectId != null) {
                    state.value.currentSubjectId?.let {
                        subjectRepository.deleteSubject(it)
                        taskRepository.deleteTasksBySubjectId(it)
                    }

                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar("Subject deleted sucessfully")
                    )
                    _snackbarEventFlow.emit(
                        SnackbarEvent.NavigateUp
                    )
                } else {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar("No subject to delete")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Couldn't delete subject. ${e.message}")
                )
            }
        }

    }

    private fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.upsertTask(
                    task = task.copy(isComplete = !task.isComplete)
                )
                if (task.isComplete) {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar("Saved in completed tasks.")
                    )
                } else {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar("Saved in completed tasks.")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Couldn't update task. ${e.message}")
                )
            }
        }
    }


}