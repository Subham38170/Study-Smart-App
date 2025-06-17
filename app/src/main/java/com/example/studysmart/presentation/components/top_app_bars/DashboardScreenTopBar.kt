package com.example.studysmart.presentation.components.top_app_bars

import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import com.example.studysmart.presentation.ui.theme.Typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenTopBar(){
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "StudySmart",
                style = Typography.headlineMedium
            )
        }

    )
}

