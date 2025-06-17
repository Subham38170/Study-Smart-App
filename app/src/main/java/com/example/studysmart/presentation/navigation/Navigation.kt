package com.example.studysmart.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.example.studysmart.presentation.dashboard.DashBoardScreen
import com.example.studysmart.presentation.dashboard.DashBoardViewModel
import com.example.studysmart.presentation.session.SessionScreen
import com.example.studysmart.presentation.session.SessionViewModel
import com.example.studysmart.presentation.session.StudySessionTimerService
import com.example.studysmart.presentation.subject.SubjectScreen
import com.example.studysmart.presentation.subject.SubjectViewModel
import com.example.studysmart.presentation.task.TaskScreen
import com.example.studysmart.presentation.task.TaskViewModel


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    timerService: StudySessionTimerService
) {
    NavHost(
        navController = navController, startDestination = Routes.DashBoardScreen
    ) {
        composable<Routes.DashBoardScreen> {
            val dashBoardViewModel: DashBoardViewModel = hiltViewModel()
            val state by dashBoardViewModel.state.collectAsStateWithLifecycle()

            DashBoardScreen(
                state = state,
                onEvent = dashBoardViewModel::onEvent,
                onSubjectCardClick = { subjectId ->
                    subjectId?.let {
                        navController.navigate(route = Routes.SubjectSceen(subjectId))
                    }
                },
                snackBarEvent = dashBoardViewModel.snackbarEventFlow,
                onTaskCardClick = { taskId ->
                    navController.navigate(
                        route = Routes.TaskScreen(
                            taskId = taskId, subjectId = null
                        )
                    )
                },
                onStartSessionButtonClick = {
                    navController.navigate(route = Routes.SessionScreen(0))
                })
        }
        composable<Routes.SessionScreen>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "study_smart://dashboard/session/{subjectId}" }
            )
        ) {
            var isBackClicked by remember { mutableStateOf(false) }
            val viewModel: SessionViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            SessionScreen(
                onBackButtonClick = {
                    if (!isBackClicked) {
                        navController.popBackStack()
                        isBackClicked = true
                    }
                },
                timerService = timerService,
                state = state,
                onEvent = viewModel::onEvent,
                snackBarEvent = viewModel.snackbarEventFlow
            )
        }
        composable<Routes.SubjectSceen> {
            var isBackClicked by remember { mutableStateOf(false) }

            val data = it.toRoute<Routes.SubjectSceen>()
            val viewmodel: SubjectViewModel = hiltViewModel<SubjectViewModel>()
            val state by viewmodel.state.collectAsStateWithLifecycle()
            SubjectScreen(
                onEvent = viewmodel::onEvent, state = state, onBackButtonClick = {
                    if (!isBackClicked) {
                        navController.popBackStack()
                        isBackClicked = true
                    }
                }, onAddTaskButtonClick = {
                    navController.navigate(
                        route = Routes.TaskScreen(
                            taskId = null, subjectId = data.subjectId
                        )
                    )
                }, onTaskCardClick = { taskId, subjectId ->
                    navController.navigate(
                        route = Routes.TaskScreen(
                            taskId = taskId, subjectId = subjectId
                        )
                    )
                }, snackbarEvent = viewmodel.snackbarEventFlow
            )
        }
        composable<Routes.TaskScreen> {
            var isBackClicked by remember { mutableStateOf(false) }

            val viewModel: TaskViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            TaskScreen(
                onBackButtonClick = {
                    if (!isBackClicked) {
                        navController.popBackStack()
                        isBackClicked = true
                    }
                },
                state = state,
                onEvent = viewModel::onEvent,
                snackBarEvent = viewModel.snackbarEventFlow,
            )
        }
    }


}