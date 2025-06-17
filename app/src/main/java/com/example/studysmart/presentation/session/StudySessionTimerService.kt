package com.example.studysmart.presentation.session

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.SyncStateContract
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.example.studysmart.util.Constants
import com.example.studysmart.util.Constants.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import java.time.temporal.TemporalUnit
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

@AndroidEntryPoint
class StudySessionTimerService : Service() {

    private val binder = StudySessionTimerBinder()

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private lateinit var timer: Timer
        private set

    var duration: Duration = Duration.ZERO
        private set

    var seconds = mutableStateOf("00")
        private set

    var minutes = mutableStateOf("00")
        private set
    var hours = mutableStateOf("00")
        private set

    var currentTimerState = mutableStateOf(TimerState.IDLE)
        private set

    override fun onBind(intent: Intent?): IBinder? = binder


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action.let {
            when (it) {
                Constants.ACTION_SERVICE_START -> {
                    startForegroundService()
                    startTimer { hours, minutes, seconds ->
                        updateNotification(hours,minutes,seconds)
                    }
                }

                Constants.ACTION_SERVICE_STOP -> {
                    stopTimer()
                }

                Constants.ACTION_SERVICE_CANCEL -> {
                    stopTimer()
                    cancelTime()
                    stopForegroundService()
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)

    }

    private fun stopTimer(){
        if(currentTimerState.value==TimerState.STARTED)
            timer.cancel()
        currentTimerState.value = TimerState.STOPPED
    }
    private fun cancelTime(){
        duration = Duration.ZERO
        updateTimerUnits()
        currentTimerState.value = TimerState.IDLE
    }

    private fun updateNotification(
        hours: String,
        minutes: String,
        seconds: String
    ){
        notificationManager.notify(
            Constants.NOTIFICATION_ID,
            notificationBuilder
                .setContentText("$hours:$minutes:$seconds")
                .build()
        )
    }
    inner class StudySessionTimerBinder: Binder(){
        fun getService(): StudySessionTimerService = this@StudySessionTimerService
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun stopForegroundService(){
        notificationManager.cancel(NOTIFICATION_ID)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


    private fun startTimer(
        onTick: (h: String, m: String, s: String) -> Unit
    ) {
        currentTimerState.value = TimerState.STARTED
        timer = fixedRateTimer(initialDelay = 1000, period = 1000) {
            duration = duration.plusSeconds(1)
            updateTimerUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }
    }


    private fun updateTimerUnits() {
        val totalSeconds = duration.seconds
        val hrs = totalSeconds / 3600
        val mins = (totalSeconds % 3600) / 60
        val secs = totalSeconds % 60

        this@StudySessionTimerService.hours.value = hrs.toInt().pad()
        this@StudySessionTimerService.minutes.value = mins.toInt().pad()
        this@StudySessionTimerService.seconds.value = secs.toInt().pad()
    }
}


enum class TimerState {
    IDLE,
    STARTED,
    STOPPED
}
fun Int.pad(): String{
    return this.toString().padStart(2,'0')
}