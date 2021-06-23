package de.htwberlin.learningcompanion.userinterface.viewmodel

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.htwberlin.learningcompanion.persistance.model.LearningSession
import de.htwberlin.learningcompanion.persistance.repository.LearningSessionRepository
import de.htwberlin.learningcompanion.userinterface.event.SensorBroadcastReceiver
import de.htwberlin.learningcompanion.userinterface.fragment.learning.SessionEvaluator
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Max on 2019-06-02.
 *
 * @author Max Oehme
 */
class LearningSessionViewModel(
        val repository: LearningSessionRepository,
        val sensorRepository: SensorBroadcastReceiver
): ViewModel() {

    val currentLearningSession: MutableLiveData<LearningSession> by lazy {
        MutableLiveData<LearningSession>()
    }

    val learningSessions: LiveData<List<LearningSession>> by lazy {
        repository.getAllLearningSessionsLiveData()
    }


    var currentLearningGoalEndTime: Long? = null
    var currentLearningGoalStartTime: Long = 0L
    val goalProgressTime: MutableLiveData<Long?> by lazy {
        MutableLiveData<Long?>()
    }

    fun isSessionRunning() : Boolean = currentLearningGoalEndTime != null


    fun startSession() {
        loadNewestSession()

        currentLearningGoalStartTime = Calendar.getInstance().time.time
        repository.getGoalByID(currentLearningSession.value?.goalID ?: -1L) {
            currentLearningGoalEndTime = (it?.durationInMin?.toLong() ?: 0) * 60 * 1000

            handler.post(checkGoalTime)
        }
    }

    fun endSession() {
        currentLearningGoalEndTime = null
    }

    fun loadNewestSession() {
        repository.getNewestLearningSession {
            currentLearningSession.value = it
        }
    }

    val handler = Handler()
    val checkGoalTime = object : Runnable {
        override fun run() {
            if (currentLearningGoalEndTime != null) {
                val currentTime = Calendar.getInstance().time.time
                val timeSinceStart = currentTime - currentLearningGoalStartTime
                val endTimeAt = currentLearningGoalEndTime ?: 0

                goalProgressTime.value = timeSinceStart
                if (timeSinceStart <= endTimeAt) {
                    loadNewestSession()

                    handler.postDelayed(this, 1000)
                } else {

                    finishRunningGoal()
                }
            } else {
                finishRunningGoal()
            }
        }
    }


    fun finishRunningGoal() {
        currentLearningSession.value?.studyDuration = goalProgressTime.value ?: 0L

        goalProgressTime.value = null
        currentLearningGoalEndTime = null
    }

}