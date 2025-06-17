package com.example.studysmart.presentation.dashboard

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.domain.model.Task
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.SubjectRepository
import com.example.studysmart.domain.repository.TaskRepository
import com.example.studysmart.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepostiory: TaskRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    val state = combine(
        _state,
        subjectRepository.getTotalSubjectCount(),
        subjectRepository.getTotalGoalHoures(),
        subjectRepository.getAllSubject(),
        sessionRepository.getTotalSessionsDuration(),
    ) { _state, subjectCount, goalHours, subjects, totalSessionDuration ->
        _state.copy(
            totalSubjectCount = subjectCount,
            totalGoalStudyHours = goalHours,
            subjects = subjects,
            totalStudiedHours = totalSessionDuration.toFloat()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

    init {
        fetchTask()
        fetchSessions()

    }


    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.DeleteSession -> {
                deleteSession()
            }

            is DashboardEvent.OnDeleteSessionButtonClick -> {
                _state.update { it.copy(session = event.session) }
            }

            is DashboardEvent.OnGoalStudyHoursChange -> {
                _state.update { it.copy(goalStudyHours = event.hours) }
            }

            is DashboardEvent.OnSubjectCardColorChange -> {
                _state.update { it.copy(subjectCardColors = event.colors) }
            }

            is DashboardEvent.OnSubjectNameChange -> {
                _state.update { it.copy(subjectName = event.name) }
            }

            is DashboardEvent.OnTaskCompleteChange -> {
                updateTask(event.task)
            }

            DashboardEvent.SaveSubject -> {
                saveSubject()
            }
        }
    }

    private fun fetchTask() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepostiory.getAllUpcomingTasks().collectLatest { tasks ->
                    _state.update { it.copy(tasks = tasks) }
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't delete session. ${e.message}"
                    )
                )
            }
        }
    }

    private fun fetchSessions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sessionRepository.getAllSessions().collectLatest { sessions ->
                    _state.update { it.copy(sessions = sessions) }
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't delete session. ${e.message}"
                    )
                )
            }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch(Dispatchers.IO) {
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

    private fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepostiory.upsertTask(
                    task = task.copy(isComplete = !task.isComplete)
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Saved in completed tasks.")
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Couldn't update task. ${e.message}")
                )
            }
        }
    }

    private fun saveSubject() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )
                _state.update {
                    it.copy(
                        subjectName = "",
                        goalStudyHours = "",
                        subjectCardColors = Subject.subjectCardColors.random()
                    )
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Subject saved successfully")
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Couldn't save subject. ${e.message}")
                )
            }
        }
    }
}