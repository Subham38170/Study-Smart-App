package com.example.studysmart.presentation.task

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.studysmart.domain.model.Task
import com.example.studysmart.domain.repository.SubjectRepository
import com.example.studysmart.domain.repository.TaskRepository
import com.example.studysmart.presentation.navigation.Routes
import com.example.studysmart.presentation.session.SessionState
import com.example.studysmart.util.Priority
import com.example.studysmart.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()
    val subjectId = savedStateHandle.toRoute<Routes.TaskScreen>().subjectId
    val taskId = savedStateHandle.toRoute<Routes.TaskScreen>().taskId

    private val _state = MutableStateFlow(TaskState())
    val state = combine(
        _state,
        subjectRepository.getAllSubject()
    ) { state, subjects ->
        state.copy(
            subjects = subjects,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TaskState()
    )

    init {
        fetchTask()
       fetchSubject()
    }



    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.DeleteTask -> {
                deleteTask()
            }

            is TaskEvent.OnDateChange -> {
                _state.update { it.copy(dueDate = event.millis) }
            }

            is TaskEvent.OnDescriptionChange -> {
                _state.update { it.copy(description = event.description) }
            }

            is TaskEvent.OnIsCompleteChange -> {
                _state.update { it.copy(isTaskComplete = !state.value.isTaskComplete) }
            }

            is TaskEvent.OnPriorityChange -> {
                _state.update { it.copy(priority = event.priority) }
            }

            is TaskEvent.OnRelatedSubjectSelect -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )

                }
            }

            is TaskEvent.OnTitleChange -> {
                _state.update {
                    it.copy(title = event.title)
                }
            }

            is TaskEvent.SaveTask -> {
                SaveTask()
            }

        }
    }

    private fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentTaskId = state.value.currentTaskId
                if (currentTaskId != null) {
                    state.value.currentTaskId?.let {
                        taskRepository.deleteTask(it)
                    }
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar("Task deleted sucessfully")
                    )
                    _snackbarEventFlow.emit(
                        SnackbarEvent.NavigateUp
                    )
                } else {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar("No Task to delete")
                    )
                }
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Couldn't delete Task. ${e.message}")
                )
            }
        }
    }


    private fun fetchSubject() {
        viewModelScope.launch {
            state.value.subjectId?.let { id ->
                subjectRepository.getSubjectById(id)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectId = subject.subjectId,
                            relatedToSubject = subject.name
                        )
                    }
                }
            }
        }
    }

    private fun SaveTask() {
        Log.d("Sjjljlg",state.value.subjectId.toString())
        viewModelScope.launch(Dispatchers.IO) {

            if (state.value.subjectId == null || state.value.relatedToSubject == null) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Please select subjecet related to the task")
                )
                return@launch
            }
            try {

                taskRepository.upsertTask(
                    task = Task(
                        title = state.value.title,
                        description = state.value.description,
                        dueDate = state.value.dueDate ?: Instant.now().toEpochMilli(),
                        priority = state.value.priority.value,
                        relatedToSubject = state.value.relatedToSubject!!,
                        isComplete = state.value.isTaskComplete,
                        taskSubjectId = state.value.subjectId!!,
                        taskId = state.value.currentTaskId ?: 0,
                    )

                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Task Saved Sucessfully")
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.NavigateUp
                )
            } catch (e: Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        " Couldn't save Task. ${e.message}",
                        SnackbarDuration.Long
                    )
                )
            }

        }
    }


    private fun fetchTask() {
        viewModelScope.launch {
            taskRepository.getTaskById(taskId?: -1)?.let { task ->
                _state.update {
                    it.copy(
                        title = task.title,
                        description = task.description,
                        dueDate = task.dueDate,
                        isTaskComplete = task.isComplete,
                        relatedToSubject = task.relatedToSubject,
                        priority = Priority.fromInt(task.priority),
                        subjectId = task.taskSubjectId,
                        currentTaskId = task.taskId
                    )
                }
            }
        }

    }


}