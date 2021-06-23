package de.htwberlin.learningcompanion.userinterface.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import de.htwberlin.learningcompanion.userinterface.activity.MainActivity
import android.content.IntentFilter
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import de.htwberlin.learningcompanion.COMM_DISTRACTION_EVENT
import de.htwberlin.learningcompanion.COMM_SENSORDATA_PROCESSED
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.persistance.model.LearningSession
import de.htwberlin.learningcompanion.persistance.repository.LearningSessionRepository
import de.htwberlin.learningcompanion.sensordata.SensorValues
import de.htwberlin.learningcompanion.sensordata.processing.ProcessorNames
import de.htwberlin.learningcompanion.sensordata.processing.processor.MovementProcessor
import de.htwberlin.learningcompanion.sensordata.processing.processor.OrientationProcessor
import de.htwberlin.learningcompanion.userinterface.event.SensorBroadcastReceiver
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.util.DISTRATION_HIGH_THRESHOLD
import de.htwberlin.learningcompanion.util.EVENT_DISTRACTION_ALERT
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.getGoalText
import java.io.IOException
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.HashMap


class LearningSeassionService : Service() {

    companion object {
        const val SESSION_CHANNEL_ID = "SESSION_CHANNEL_ID"
        const val DEFAULT_CHANNEL_ID = "DEFAULT_CHANNEL_ID"

        const val RUNNING_NOTIFY_ID = 1
        const val FINISHED_NOTIFY_ID = 2
        const val DISTRACTION_NOTIFY_ID = 3
    }

    private lateinit var learningSessionRepository: LearningSessionRepository
    private val sensorBroadcastReceiver = SensorBroadcastReceiver()

    private var distractionUpdateTimer = 0L

    private var mRecorder: MediaRecorder? = null
    private var isNotificationSent = false
    private var isDitractionNotificationSent = false

    override fun onCreate() {
        super.onCreate()

        learningSessionRepository = LearningSessionRepository(this.applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotification(intent)

        registerReceiver(sensorBroadcastReceiver, IntentFilter().apply {
            addAction(COMM_SENSORDATA_PROCESSED)
        })

        sensorBroadcastReceiver.onProcessedSensorUpdateListener = onSensorUpdate
        startRecording()
        isNotificationSent = false

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        unregisterReceiver(sensorBroadcastReceiver)
        stopRecording()
        super.onDestroy()
    }

    private fun createNotification(intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    SESSION_CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, SESSION_CHANNEL_ID)
                .setContentTitle("Goal Running")
                .setContentText("You Goal is: ${getGoalText(ViewModelFactory.GOAL_VIEW_MODEL_INSTANCE.currentGoal.value)}")
                .setSmallIcon(R.drawable.ic_launcher_charly_foreground)
                .setContentIntent(pendingIntent)
                .build()

        startForeground(RUNNING_NOTIFY_ID, notification)

    }

    private fun sendDoneNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    DEFAULT_CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setContentTitle("Goal Done! Please Evaluate")
                .setContentText("You finished Goal: ${getGoalText(ViewModelFactory.GOAL_VIEW_MODEL_INSTANCE.currentGoal.value)}")
                .addAction(R.drawable.ic_goal, "Evaluate", pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_charly_foreground)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setVibrate(longArrayOf(0, 1500, 500, 1500))
                .build()

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(FINISHED_NOTIFY_ID, notification)
        }
    }

    private val onSensorUpdate: (HashMap<String,String>) -> Unit = {update ->
        val time = Calendar.getInstance().timeInMillis
        val movement = update[ProcessorNames.MOVEMENT.name] ?: MovementProcessor.State.IDLE.name
        val orientation = update[ProcessorNames.ORENTATION.name] ?: MovementProcessor.State.IDLE.name
        val light = update[ProcessorNames.LIGHT.name]?.toFloat() ?: 0f
        val noise = getAmplitude().toFloat()


        if (ViewModelFactory.LEARNINGSESSION_VIEW_MODEL_INSTANCE.isSessionRunning()) {
            if (time - distractionUpdateTimer > 1000L) {
                distractionUpdateTimer = time
                val distraction = calculateDistraction(time, orientation)
                learningSessionRepository.updateDistractionInLatestSession(distraction)

            }

            learningSessionRepository.updateSensorValuesInLatestSession(time, orientation, movement, light, noise)
        } else {
            if (isNotificationSent.not()) {
                isNotificationSent = true
                sendDoneNotification()

                stopForeground(true)
                stopSelf()
            }
        }
    }

    private fun checkDistractionThreshold(time: Long, distraction: Int) {
        if (distraction >= DISTRATION_HIGH_THRESHOLD) {

            if (isDitractionNotificationSent.not()) {

                learningSessionRepository.updateEventInLatestSession(time, EVENT_DISTRACTION_ALERT)

                val intent = Intent(COMM_DISTRACTION_EVENT).apply {
                    putExtra(EVENT_DISTRACTION_ALERT, true)
                }
                sendBroadcast(intent)

                isDitractionNotificationSent = true

                val notificationIntent = Intent(this, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

                val notification = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                        .setContentTitle("${SharedPreferencesHelper.get(this).getUserName()}, please focus on your goal!")
                        .setSmallIcon(R.drawable.ic_launcher_charly_foreground)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
                        .build()



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val serviceChannel = NotificationChannel(
                            DEFAULT_CHANNEL_ID,
                            "Default Channel",
                            NotificationManager.IMPORTANCE_DEFAULT
                    )

                    val manager = getSystemService(NotificationManager::class.java)
                    manager.createNotificationChannel(serviceChannel)
                }

                with(NotificationManagerCompat.from(this)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(DISTRACTION_NOTIFY_ID, notification)
                }

                Handler().postDelayed({
                    if (isDitractionNotificationSent) {
                        isDitractionNotificationSent = false
                        NotificationManagerCompat.from(this).cancel(DISTRACTION_NOTIFY_ID)
                    }
                }, 20000)
            }

        } else {
            isDitractionNotificationSent = false
            NotificationManagerCompat.from(this).cancel(DISTRACTION_NOTIFY_ID)
        }
    }

    private fun calculateDistraction(time: Long, orientation: String) : Int {

            return if (orientation == OrientationProcessor.State.PICK_UP.name) {
                val inc = increaseDistraction()
                checkDistractionThreshold(time, inc)
                inc
            } else {
                decreaseDistraction()
            }
    }

    fun increaseDistraction(): Int {
        val session = learningSessionRepository.getNewestLearningSession()

        return session.distractionLevel.last() + 1
    }

    fun decreaseDistraction(): Int {
        val session = learningSessionRepository.getNewestLearningSession()

        return session.distractionLevel.let {
            if (it.last() > 0) {
                if (it.size > 5) {
                    var dec = false
                    for (i in it.size - 5 until it.size) {
                        if (it[i] < it.last()) dec = true
                    }

                    if (dec) {
                        it.last()
                    } else {
                        it.last() - 1
                    }
                } else {
                    it.last()
                }
            } else {
                0
            }
        }
    }


    private fun startRecording() {
        mRecorder?.apply {
            release()
        }
        mRecorder = null
        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile("/dev/null")
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            start()
        }
    }


    private fun stopRecording() {
        mRecorder?.apply {
            stop()
            release()
        }
        mRecorder = null
    }

    private fun getAmplitude(): Int {
        return if (mRecorder != null)
            mRecorder!!.maxAmplitude
        else
            0
    }
}
