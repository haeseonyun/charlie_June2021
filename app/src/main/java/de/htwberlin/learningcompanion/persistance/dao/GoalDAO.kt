package de.htwberlin.learningcompanion.persistance.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.htwberlin.learningcompanion.persistance.model.Goal

@Dao
interface GoalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoal(goal: Goal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoals(goal: List<Goal>)

    @Update
    fun updateGoal(goal: Goal)

    @Update
    fun updateGoals(goals: List<Goal>)

    @Delete
    fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goal WHERE id == :id")
    fun getGoalByID(id: Long): Goal

    @Query("SELECT * FROM goal")
    fun getGoals(): List<Goal>

    @Query("SELECT * FROM goal")
    fun getGoalsAsLiveData(): LiveData<List<Goal>>

    @Query("SELECT * FROM goal ORDER BY id DESC LIMIT 1")
    fun getNewestGoal(): Goal
}