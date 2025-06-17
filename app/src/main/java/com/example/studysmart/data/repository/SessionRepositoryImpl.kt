package com.example.studysmart.data.repository

import com.example.studysmart.data.local.SessionDao
import com.example.studysmart.domain.model.Session
import com.example.studysmart.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
): SessionRepository {
    override suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions()
            .map { sessions -> sessions.sortedByDescending { it.date } }
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions()
            .map { sessions -> sessions.sortedByDescending { it.date } }
            .take(5)
    }

    override fun getTotalSessionsDuration(): Flow<Long> {
        return sessionDao.getTotalSessionsDuration()
    }

    fun getTotalSessionDurationBySubject(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionDurationBySubjectId(subjectId)
    }

    override fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>> {
        return sessionDao.getAllSessions()
            .map { sessions -> sessions.sortedByDescending { it.date } }
            .take(10)
    }

    override fun getRecentSessionForSubject(subjectId: Int): Flow<List<Session>> {
        return sessionDao.getRecentSessionsForSubject(subjectId)
    }

    override fun getTotalSessionDurationBySubjectId(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionDurationBySubjectId(subjectId)
    }

    fun deleteSessionsBySubjectId(subjectId: Int) {
        sessionDao.deleteSessionsBySubjectId(subjectId)
    }
}