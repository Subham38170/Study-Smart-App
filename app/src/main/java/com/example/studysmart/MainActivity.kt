package com.example.studysmart

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.example.studysmart.domain.model.Session
import com.example.studysmart.domain.model.Subject
import com.example.studysmart.domain.model.Task
import com.example.studysmart.presentation.navigation.Navigation
import com.example.studysmart.presentation.ui.theme.StudySmartTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.jar.Manifest

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.studysmart.presentation.session.StudySessionTimerService
import kotlin.concurrent.timer

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isBound by mutableStateOf(false)
    private lateinit var timerService: StudySessionTimerService
    private val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as StudySessionTimerService.StudySessionTimerBinder
            timerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, StudySessionTimerService::class.java).also{
            intent->
            bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)

        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
        isBound = false
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if(isBound) {
                StudySmartTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val navController = rememberNavController()
                        Navigation(
                            navController = navController,
                            timerService = timerService
                        )
                    }
                }
            }
        }
        requestPermission()
    }
    private fun requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

}

val subjects = listOf(
    Subject("English", 10f, Subject.subjectCardColors[0].map { it.toArgb() }, 0),
    Subject("Physics", 10f, Subject.subjectCardColors[1].map { it.toArgb() }, 0),
    Subject("Maths", 10f, Subject.subjectCardColors[2].map { it.toArgb() }, 0),
    Subject("Geology", 10f, Subject.subjectCardColors[3].map { it.toArgb() }, 0),
    Subject("Fine Arts", 10f, Subject.subjectCardColors[4].map { it.toArgb() }, 0)
)
