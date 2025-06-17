package com.example.studysmart.presentation.navigation

import com.example.studysmart.domain.model.Task
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    data class SubjectSceen(
        val subjectId: Int
    ): Routes()

    @Serializable
    data class TaskScreen(
        val taskId: Int?,
        val subjectId: Int?
    ): Routes()

    @Serializable
    object DashBoardScreen: Routes()

    @Serializable
    data class SessionScreen(val subjectId: Int): Routes()
}