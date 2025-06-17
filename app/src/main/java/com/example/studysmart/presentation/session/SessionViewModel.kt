package com.example.studysmart.presentation.session

import android.util.Log
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.domain.model.Session
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.SubjectRepository
import com.example.studysmart.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SessionState())
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    val state = combine(
        _state,
        subjectRepository.getAllSubject(),
        sessionRepository.getAllSessions()
    ) { state, subjects, sessions ->
        state.copy(
            subjects = subjects,
            sessions = sessions
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SessionState()
    )

    fun onEvent(event: SessionEvent) {
        when (event) {
            SessionEvent.CheckSubjectId -> {
            }

            SessionEvent.DeleteSession -> {
                deleteSession()
            }
            is SessionEvent.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }
            is SessionEvent.OnRelatedSubjectChange -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }

            is SessionEvent.SaveSession -> {
                if(state.value.relatedToSubject == null)
                    viewModelScope.launch {
                        _snackbarEventFlow.emit(
                            SnackbarEvent.ShowSnackbar(
                                message = "Select subject related to session",
                                duration = SnackbarDuration.Short
                            )
                        )
                    }
                else insertSession(event.duration)

            }

            is SessionEvent.UpdateSubjectAndRelatedSubject -> {}
        }
    }


    private fun deleteSession(){
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
            }catch (e: Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't delete session. ${e.message}"
                    )
                )
            }
        }
    }
    private fun insertSession(duration: Long) {
        Log.d("Subject", state.value.subjectId.toString())
        viewModelScope.launch {
            if (duration < 36) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Single session can not be less than the 36 seconds"
                    )
                )
                Log.d("CHECK","Lesser")
            } else {

                try {
                    sessionRepository.insertSession(
                        Session(
                            sessionSubjectId = state.value.subjectId ?: -1,
                            relatedToSubject = state.value.relatedToSubject.toString(),
                            date = Instant.now().toEpochMilli(),
                            duration = duration
                        )
                    )
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Session saved successfully")
                    )
                } catch (e: Exception) {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(
                            "Couldn't undate task. ${e.message}",
                            SnackbarDuration.Long
                        )
                    )
                }
            }
        }
    }

}