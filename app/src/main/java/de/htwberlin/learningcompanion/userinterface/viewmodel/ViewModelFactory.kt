package de.htwberlin.learningcompanion.userinterface.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.htwberlin.learningcompanion.LCApplication
import de.htwberlin.learningcompanion.persistance.repository.GoalRepository
import de.htwberlin.learningcompanion.persistance.repository.LearningSessionRepository
import de.htwberlin.learningcompanion.persistance.repository.PlaceRepository
import de.htwberlin.learningcompanion.persistance.repository.RecommendationRepository
import de.htwberlin.learningcompanion.userinterface.event.SensorBroadcastReceiver
import de.htwberlin.learningcompanion.userinterface.fragment.recommendation.RecommendationViewModel

/**
 * Created by Max on 2019-05-27.
 *
 * @author Max Oehme
 */
object ViewModelFactory : ViewModelProvider.Factory {
    val application = LCApplication.app!!

    val repositorySensor = SensorBroadcastReceiver()

    private val SENSOR_VIEW_MODEL_INSTANCE: SensorViewModel by lazy {
        SensorViewModel(repositorySensor)
    }

    val GOAL_VIEW_MODEL_INSTANCE: GoalViewModel by lazy {
        GoalViewModel(GoalRepository(application.applicationContext))
    }

    private val PLACE_VIEW_MODEL_INSTANCE: PlaceViewModel by lazy {
        PlaceViewModel(PlaceRepository(application.applicationContext))
    }

    val LEARNINGSESSION_VIEW_MODEL_INSTANCE: LearningSessionViewModel by lazy {
        LearningSessionViewModel(LearningSessionRepository(application.applicationContext), repositorySensor)
    }


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            SensorViewModel::class.java -> SENSOR_VIEW_MODEL_INSTANCE
            GoalViewModel::class.java -> GOAL_VIEW_MODEL_INSTANCE
            PlaceViewModel::class.java -> PLACE_VIEW_MODEL_INSTANCE
            LearningSessionViewModel::class.java -> LEARNINGSESSION_VIEW_MODEL_INSTANCE
            RecommendationViewModel::class.java -> RecommendationViewModel(LearningSessionRepository(application.applicationContext), RecommendationRepository(application.applicationContext))
            else -> modelClass.newInstance()
        } as T
    }
}