package de.htwberlin.learningcompanion.userinterface.fragment.learning

import android.annotation.SuppressLint
import android.app.Activity
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.LearningSessionViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.PlaceViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import org.jetbrains.anko.sensorManager
import java.util.*
import java.util.concurrent.TimeUnit


class SessionHandler private constructor(private val activity: FragmentActivity) {


    private val viewModelFactory = ViewModelFactory
    private lateinit var learningSessionViewModel: LearningSessionViewModel
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var goalViewModel: GoalViewModel

    companion object {
        private val TAG = SessionHandler::class.java.simpleName
        private val TARGETED_AMOUNT_OF_SENSOR_VALUES: Int = 200
        private val ON_UPDATE = 1
        private val ON_FINISH = 2

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: SessionHandler? = null

        fun get(activity: FragmentActivity): SessionHandler = INSTANCE
                ?: synchronized(this) {
            INSTANCE ?: SessionHandler(activity).also { INSTANCE = it }
        }
    }

    private val observerList = mutableListOf<LearningSessionObserver>()

    private lateinit var goal: Goal
    private lateinit var timer: CountDownTimer

    private var goalTargetDurationInMin = 0
    var remainingMillis = 0L

    private val sensorHandler = SensorHandler(activity.sensorManager)
    private val learningSessionEvaluator = SessionEvaluator(sensorHandler.lightDataList, sensorHandler.noiseDataList)

    var sessionRunning = false
    var measuringSensors = false

    init {
        goalViewModel = ViewModelProviders.of(activity, viewModelFactory).get(GoalViewModel::class.java)
        placeViewModel = ViewModelProviders.of(activity, viewModelFactory).get(PlaceViewModel::class.java)
        learningSessionViewModel = ViewModelProviders.of(activity, viewModelFactory).get(LearningSessionViewModel::class.java)
    }

    fun canStartLearningSession(): Boolean {
        val currentGoal = goalViewModel.currentGoal.value
        val currentPlace = placeViewModel.currentPlace.value

        return when {
            currentGoal == null -> false
            currentPlace == null -> false
            else -> true
        }
    }

    fun startLearningSessionWithMeasuringSensors() {
        if (!sessionRunning) {
            sessionRunning = true
            measuringSensors = true
            retrieveInformationFromGoal()
            startTimer()

            sensorHandler.clear()
            sensorHandler.start(calculateIntervalForSession())
        }
    }

    private fun calculateIntervalForSession(): Int {
        val goalTargetDurationInMin = getGoalTargetDuration(goal)
        val goalTargetDurationInSec = goalTargetDurationInMin * 60
        return Math.max(goalTargetDurationInSec / TARGETED_AMOUNT_OF_SENSOR_VALUES, 1) // value has to >= 1
    }

    fun startLearningSessionWithoutMeasuringSensors() {
        if (!sessionRunning) {
            sessionRunning = true
            measuringSensors = false
            retrieveInformationFromGoal()
            startTimer()
        }
    }

    private fun retrieveInformationFromGoal() {
        goal = goalViewModel.currentGoal.value!!

        goalTargetDurationInMin = getGoalTargetDuration(goal)
    }

    private fun getGoalTargetDuration(goal: Goal): Int {
        if (goal.durationInMin == null) {
            goal.durationInMin = calculateDurationUntilTimestamp(goal.untilTimeStamp!!)
            goalViewModel.repository.updateGoal(goal)
        }
        return goal.durationInMin!!
    }

    private fun calculateDurationUntilTimestamp(timestamp: String): Int {
        val rightNow = Calendar.getInstance()
        val futureTime = Calendar.getInstance()

        var timeStrings = timestamp.split(":")

        futureTime.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH), timeStrings[0].toInt(), timeStrings[1].toInt())

        if (!rightNow.before(futureTime)) {
            futureTime.set(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH) + 1, timeStrings[0].toInt(), timeStrings[1].toInt())
        }

        var currentDate = rightNow.time
        var futureDate = futureTime.time

        var diffMs = futureDate.time - currentDate.time
        var diffSec = diffMs / 1000
        var min = diffSec / 60
        var sec = diffSec % 60

        System.out.println("The difference is " + min + " minutes and " + sec + " seconds.");
        return min.toInt()

    }

    private fun startTimer() {
        timer = object : CountDownTimer(TimeUnit.MINUTES.toMillis(goalTargetDurationInMin.toLong()), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                notifyObservers(ON_UPDATE)
                Log.d(TAG, "tick called")
            }

            override fun onFinish() {
                notifyObservers(ON_FINISH)
            }
        }

        timer.start()
    }

    fun observe(observer: LearningSessionObserver) {
        observerList.add(observer)
    }

    private fun notifyObservers(notifyCase: Int) {
        if (notifyCase == ON_UPDATE)
            observerList.forEach {
                it.onUpdate(remainingMillis)
            }
        else if (notifyCase == ON_FINISH)
            observerList.forEach {
                it.onFinish()
            }
    }

    fun stopLearningSession() {
        if (sessionRunning) {
            sessionRunning = false
            sensorHandler.stop()
            timer.cancel()
        }
    }

    fun getSessionInfo(): String {
        val lightLevel = getLightLevel()
        val noiseLevel = getNoiseLevel()

        val timeString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(remainingMillis), TimeUnit.MILLISECONDS.toMinutes(remainingMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingMillis)), TimeUnit.MILLISECONDS.toSeconds(remainingMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingMillis)))

        return if (measuringSensors)
            "Light: ${lightLevel.levelName} \nNoise: ${noiseLevel.levelName} \nRemaining time: $timeString"
        else
            "Remaining time: $timeString"
    }

    fun getLightLevel(): LightLevel {
        return learningSessionEvaluator.evaluateLight()
    }

    fun getNoiseLevel(): NoiseLevel {
        return learningSessionEvaluator.evaluateNoise()
    }

    fun getLightValues(): ArrayList<Float> {
        return sensorHandler.lightDataList
    }


    fun getNoiseValues(): ArrayList<Float> {
        return sensorHandler.noiseDataList
    }

    interface LearningSessionObserver {
        fun onUpdate(millisUntilFinished: Long)
        fun onFinish()
    }
}