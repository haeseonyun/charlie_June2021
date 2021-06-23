package de.htwberlin.learningcompanion.persistance.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.htwberlin.learningcompanion.persistance.model.LearningSession

@Dao
interface LearningSessionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningSession(session: LearningSession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningSessions(places: List<LearningSession>)

    @Update
    suspend fun updateLearningSession(session: LearningSession)

    @Update
    suspend fun updateLearningSessions(places: List<LearningSession>)

    @Delete
    suspend fun deleteLearningSession(session: LearningSession)

    @Query("SELECT * FROM session WHERE id == :id")
    fun getLearningSessionByID(id: Long): LearningSession

    @Query("SELECT * FROM session WHERE place_id == :placeID AND goal_id == :goalID")
    fun getLearningSessionByGoalAndPlaceID(goalID: Long, placeID: Long): List<LearningSession>

    @Query("SELECT * FROM session ORDER BY id DESC LIMIT 1")
    fun getLatestLearningSession(): LearningSession

    @Query("SELECT * FROM session ORDER BY id DESC LIMIT 1")
    suspend fun getNewestLearningSession(): LearningSession

    @Query("SELECT * FROM session ORDER BY id DESC LIMIT 1")
    fun getNewestLearningSessionLiveData(): LiveData<LearningSession>

    @Query("SELECT * FROM session WHERE place_id == :id")
    fun getLearningSessionByPlaceID(id: Long): List<LearningSession>

    @Query("SELECT * FROM session WHERE goal_id == :id")
    fun getLearningSessionByGoalID(id: Long): List<LearningSession>

    @Query("SELECT * FROM session")
    fun getLearningSessions(): List<LearningSession>

    @Query("SELECT * FROM session ORDER BY createdAtTimestamp DESC")
    fun getLearningSessionsAsLiveData(): LiveData<List<LearningSession>>
}