package com.example.studysmart.domain.repository

import com.example.studysmart.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(session: Session)

    suspend fun deleteSession(session: Session)

    fun getAllSessions(): Flow<List<Session>>

    fun getRecentFiveSessions(): Flow<List<Session>>

    fun getTotalSessionsDuration(): Flow<Long>

    fun getTotalSessionDurationBySubjectId(subjectId: Int): Flow<Long>

    fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>>

    fun getRecentSessionForSubject(subjectId: Int): Flow<List<Session>>
}