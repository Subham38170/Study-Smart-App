package com.example.studysmart.presentation.session

import com.example.studysmart.domain.model.Session
import com.example.studysmart.domain.model.Subject

sealed class SessionEvent {
    data class OnRelatedSubjectChange(val subject: Subject): SessionEvent()

    data class SaveSession(val duration: Long): SessionEvent()

    data class OnDeleteSessionButtonClick(val session: Session): SessionEvent()

    data object DeleteSession: SessionEvent()

    data object CheckSubjectId: SessionEvent()

    data class UpdateSubjectAndRelatedSubject(
        val subjectId: Int?,
        val relatedToSubject: String?
    ): SessionEvent()
}