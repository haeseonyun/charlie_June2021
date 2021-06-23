package de.htwberlin.learningcompanion.persistance.dao

import androidx.room.Dao
import androidx.room.Query
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.Place

@Dao
interface RecommendationDAO {

    @Query("SELECT * FROM goal g, session s WHERE s.goal_id = g.id AND s.userRating >= 70 GROUP BY g.id ORDER BY s.userRating DESC")
    fun getGoalsByDescendingUserRating(): List<Goal>

    @Query("SELECT * FROM place p, session s, goal g WHERE s.userRating >= 70 AND s.goal_id = g.id AND s.place_id = p.id GROUP BY p.id HAVING sum(s.userRating) > 0 ORDER BY sum(s.userRating) DESC")
    fun getPlacesByDescendingUserRating(): List<Place>

    @Query("SELECT studyDuration FROM session s WHERE s.userRating = ( SELECT max(userRating) FROM (SELECT * FROM session WHERE studyDuration >= 60000))")
    fun getBestDuration(): Long

}