package de.htwberlin.learningcompanion.userinterface.fragment.recommendation

import androidx.lifecycle.ViewModel
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.LearningSession
import de.htwberlin.learningcompanion.persistance.model.Place
import de.htwberlin.learningcompanion.persistance.repository.LearningSessionRepository
import de.htwberlin.learningcompanion.persistance.repository.RecommendationRepository
import de.htwberlin.learningcompanion.userinterface.fragment.learning.SessionEvaluator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Max on 2019-06-27.
 *
 * @author Max Oehme
 */
class RecommendationViewModel(
        val sessionRepository: LearningSessionRepository,
        val recommendationRepository: RecommendationRepository
): ViewModel() {

    var bestGoals: List<Goal>? = null
    var bestPlaces: List<Place>? = null
    var bestTime: String? = null
    var bestDuration: Int = 0
    var bestBrightnessValue: Int = 0
    var bestNoiseValue: Int = 0
    var topSuccessFactor = ArrayList<String>()
    var topIssues = ArrayList<String>()

    fun calculateRecommendation(onCalculationFinished: (() -> Unit) ) {
        val sessionsList = sessionRepository.getAllLearningSessions()

        bestGoals = calculateBestGoals()
        bestPlaces = calculateBestPlaces()
        bestTime = calculateBestTime(sessionsList)
        bestDuration = calculateBestDuration(sessionsList)
        bestBrightnessValue = calculateBestBrightnessValue(sessionsList)
        bestNoiseValue = calculateBestNoiseValue(sessionsList)
        calculateTopSuccesAndIssues(sessionsList)

        onCalculationFinished.invoke()
    }

    private fun calculateBestGoals(): List<Goal> {
        val goalsByDescendingUserRating = recommendationRepository.getGoalsByDescendingUserRating()

        val endIndex = Math.min(goalsByDescendingUserRating.size, 3)

        return goalsByDescendingUserRating.subList(0, endIndex)
    }

    private fun calculateBestPlaces(): List<Place> {
        return recommendationRepository.getPlacesByDescendingUserRating()
    }

    private fun calculateBestTime(sessionsList: List<LearningSession>): String {

        val morningSessions = mutableListOf<LearningSession>()
        val afternoonSessions = mutableListOf<LearningSession>()
        val eveningSessions = mutableListOf<LearningSession>()
        val nightOwlSessions = mutableListOf<LearningSession>()
        sessionsList.filter {
            (it.userRating ?: 0) >= 70
        }.forEach {
            val instance = Calendar.getInstance()
            instance.time = Date(it.createdAtTimestamp)

            val hour = instance.get(Calendar.HOUR_OF_DAY)

            when (hour) {
                in 6..12 -> morningSessions.add(it)
                in 12..18 -> afternoonSessions.add(it)
                in 18..24 -> eveningSessions.add(it)
                in 0..6 -> nightOwlSessions.add(it)
            }
        }

        val averageMorningRating = calculateAverageUserRatingForSessionList(morningSessions)
        val averageAfternoonRating = calculateAverageUserRatingForSessionList(afternoonSessions)
        val averageEveningRating = calculateAverageUserRatingForSessionList(eveningSessions)
        val averageNightOwlRating = calculateAverageUserRatingForSessionList(nightOwlSessions)

        val average = arrayOf(
                "Morning (06-12)" to averageMorningRating,
                "Afternoon (12-18)" to averageAfternoonRating,
                "Evening (18-24)" to averageEveningRating,
                "Night owl (00-06)" to averageNightOwlRating)

        val averagePair = average.maxBy {
            it.second
        }

        return if (averagePair?.second == 0.0) {
            ""
        } else {
            averagePair?.first ?: ""
        }
    }

    private fun calculateAverageUserRatingForSessionList(sessions: List<LearningSession>): Double {
        var ratingSum = 0.0
        var count = 0

        sessions.forEach {
            ratingSum += it.userRating ?: 0
            count++
        }

        val average = ratingSum / count

        return if (average.equals(Double.NaN)) {
            0.0
        } else
            average
    }

    private fun calculateBestDuration(sessionsList: List<LearningSession>): Int {
        val session = sessionsList.filter {
            (it.userRating ?: 0) >= 70 && it.studyDuration >= 60000
        }.maxBy {
            it.userRating ?: 0
        }
        return session?.studyDuration?.div(60000)?.toInt() ?: 0
    }

    private fun calculateBestBrightnessValue(sessionsList: List<LearningSession>): Int {

        // map which maps Light value average  to USERRATING
        val mapWithBrightnessLevels = hashMapOf<Double, Int>()

        sessionsList.filter {
            (it.userRating ?: 0) >= 70
        }.forEach {
            val lightAverage = SessionEvaluator.calculateAverage(it.lightValues)
            mapWithBrightnessLevels[lightAverage] = mapWithBrightnessLevels[lightAverage]?.plus(it.userRating ?: 0) ?: it.userRating ?: 0
        }

        var bestLightAverage = 0.0
        var bestUserRatingSum = 0

        val iterator = mapWithBrightnessLevels.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key.isNaN().not()) {
                if (entry.value > bestUserRatingSum) {
                    bestLightAverage = entry.key
                    bestUserRatingSum = entry.value
                }
            }
        }

        return bestLightAverage.toInt()
    }

    private fun calculateBestNoiseValue(sessionsList: List<LearningSession>): Int {

        // map which maps Noise value average to USERRATING
        val mapWithNoiseLevels = hashMapOf<Double, Int>()

        sessionsList.filter {
            (it.userRating ?: 0) >= 70
        }.forEach {
            val noiseAverage = SessionEvaluator.calculateAverage(it.noiseValues)
            mapWithNoiseLevels[noiseAverage] = mapWithNoiseLevels[noiseAverage]?.plus(it.userRating ?: 0) ?: it.userRating ?: 0
        }

        var bestNoiseAverage = 0.0
        var bestUserRatingSum = 0

        val iterator = mapWithNoiseLevels.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key.isNaN().not()) {
                if (entry.value > bestUserRatingSum) {
                    bestNoiseAverage = entry.key
                    bestUserRatingSum = entry.value
                }
            }
        }

        return bestNoiseAverage.toInt()
    }

    private fun calculateTopSuccesAndIssues(sessionsList: List<LearningSession>) {

        val sessionSuccess = sessionsList.filter {
            (it.userRating ?: 0) >= 70 && it.evaluationSuccessFactor != null
        }.sortedByDescending {
            it.userRating
        }

        val sessionIssues = sessionsList.filter {
            (it.userRating ?: 100) <= 30 && it.evaluationSuccessFactor != null
        }.sortedBy {
            it.userRating
        }


        topSuccessFactor.apply {
            for (i in 0 until sessionSuccess.size) {
                when (i) {
                    0 -> add(sessionSuccess.component1().evaluationSuccessFactor ?: "")
                    1 -> add(sessionSuccess.component2().evaluationSuccessFactor ?: "")
                    2 -> add(sessionSuccess.component3().evaluationSuccessFactor ?: "")
                }
            }
        }

        topIssues.apply {
            for (i in 0 until sessionIssues.size) {
                when (i) {
                    0 -> add(sessionIssues.component1().evaluationSuccessFactor ?: "")
                    1 -> add(sessionIssues.component2().evaluationSuccessFactor ?: "")
                    2 -> add(sessionIssues.component3().evaluationSuccessFactor ?: "")
                }
            }
        }
    }
}