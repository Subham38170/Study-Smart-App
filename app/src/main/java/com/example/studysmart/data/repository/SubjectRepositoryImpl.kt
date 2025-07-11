package com.example.studysmart.data.repository

import com.example.studysmart.data.local.SessionDao
import com.example.studysmart.data.local.SubjectDao
import com.example.studysmart.data.local.TaskDao
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao
): SubjectRepository{
    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHoures(): Flow<Float> {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return subjectDao.getSubjectById(subjectId)
    }

    override suspend fun deleteSubject(subjectId: Int) {
        taskDao.deleteTaskBySubjectId(subjectId)
        sessionDao.deleteSessionsBySubjectId(subjectId)
        subjectDao.deleteSubject(subjectId)


    }

    override fun getAllSubject(): Flow<List<Subject>> {
        return subjectDao.getAllSubject()
    }
}