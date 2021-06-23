package de.htwberlin.learningcompanion.persistance.repository

import android.content.Context
import androidx.lifecycle.LiveData
import de.htwberlin.learningcompanion.persistance.AppDatabase
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.LearningSession
import kotlinx.coroutines.runBlocking

class LearningSessionRepository(
        val context: Context
) {
    private val appDatabase = AppDatabase.get(this.context)

    fun getAllLearningSessionsLiveData(): LiveData<List<LearningSession>> = appDatabase.sessionDao().getLearningSessionsAsLiveData()

    fun getAllLearningSessions() : List<LearningSession> {
        return appDatabase.sessionDao().getLearningSessions()
    }

    fun getLearningSessionByID(sessionID: Long): LearningSession {
        return appDatabase.sessionDao().getLearningSessionByID(sessionID)
    }

    fun getLearningSessionByGoalAndPlaceID(goalID: Long, placeID: Long): List<LearningSession> {
        return appDatabase.sessionDao().getLearningSessionByGoalAndPlaceID(goalID, placeID)
    }

    fun getNewestLearningSession(): LearningSession =
            appDatabase.sessionDao().getLatestLearningSession()

    fun getNewestLearningSession(callback: (LearningSession) -> Unit) = runBlocking {
        callback.invoke(appDatabase.sessionDao().getNewestLearningSession())
    }

    fun getNewestLearningSessionLiveData(): LiveData<LearningSession> {
        return appDatabase.sessionDao().getNewestLearningSessionLiveData()
    }

    fun getLearningSessionsByGoalID(goalID: Long): List<LearningSession> {
        return appDatabase.sessionDao().getLearningSessionByGoalID(goalID)
    }

    fun getLearningSessionsByPlaceID(placeID: Long): List<LearningSession> {
        return appDatabase.sessionDao().getLearningSessionByPlaceID(placeID)
    }

    fun insertLearningSession(session: LearningSession) = runBlocking {
        appDatabase.sessionDao().insertLearningSession(session)
    }

    fun insertLearningSession(session: LearningSession, callback: ((LearningSession) -> Unit)) = runBlocking {
        appDatabase.sessionDao().insertLearningSession(session)

        callback.invoke(appDatabase.sessionDao().getNewestLearningSession())
    }

    fun updateLearningSession(session: LearningSession) = runBlocking {
        appDatabase.sessionDao().updateLearningSession(session)
    }

    fun deleteLearningSession(session: LearningSession) = runBlocking {
        appDatabase.sessionDao().deleteLearningSession(session)
    }

    fun updateSensorValuesInLatestSession(timestamp: Long, orientation: String, movement: String, light: Float, noise: Float) = runBlocking {
        val session = appDatabase.sessionDao().getNewestLearningSession()

        session.orientationStates.put(timestamp, orientation)
        session.movementStates.put(timestamp, movement)
        (session.lightValues as ArrayList).add(light)
        (session.noiseValues as ArrayList).add(noise)

        appDatabase.sessionDao().updateLearningSession(session)
    }

    fun updateDistractionInLatestSession(distraction: Int) = runBlocking {
        val session = appDatabase.sessionDao().getNewestLearningSession()

        (session.distractionLevel as ArrayList).add(distraction)

        appDatabase.sessionDao().updateLearningSession(session)
    }


    fun updateEventInLatestSession(time: Long, event: String) = runBlocking {
        val session = appDatabase.sessionDao().getNewestLearningSession()

        session.events[time] = event

        appDatabase.sessionDao().updateLearningSession(session)
    }

    fun updateTappingEventInLatestSession(time: Long) = runBlocking {
        val session = appDatabase.sessionDao().getNewestLearningSession()

        (session.faceTapped as ArrayList).add(time)

        appDatabase.sessionDao().updateLearningSession(session)
    }

    fun getGoalByID(goalID: Long, callback: (Goal?) -> Unit) = runBlocking {
        val session = appDatabase.goalDao()

        callback.invoke(session.getGoalByID(goalID))
    }

}
