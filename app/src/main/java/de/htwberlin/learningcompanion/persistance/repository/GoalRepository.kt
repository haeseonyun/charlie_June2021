package de.htwberlin.learningcompanion.persistance.repository

import android.content.Context
import androidx.lifecycle.LiveData
import de.htwberlin.learningcompanion.persistance.AppDatabase
import de.htwberlin.learningcompanion.persistance.model.Goal

class GoalRepository(
        val context: Context
) {

    private val appDatabase: AppDatabase = AppDatabase.get(this.context)

    fun getAllGoalsLiveData() : LiveData<List<Goal>> = appDatabase.goalDao().getGoalsAsLiveData()

    fun getAllGoals() : List<Goal> {
        return appDatabase.goalDao().getGoals()
    }

    fun getGoalByID(goalID: Long): Goal {
        return appDatabase.goalDao().getGoalByID(goalID)
    }

    fun insertGoalList(goalList: List<Goal>) {
        appDatabase.goalDao().insertGoals(goalList)
    }

    fun insertGoal(goal: Goal) {
        appDatabase.goalDao().insertGoal(goal)
    }

    fun insertGoal(goal: Goal, callback: ((Goal) -> Unit)) {
        appDatabase.goalDao().insertGoal(goal)

        callback.invoke(appDatabase.goalDao().getNewestGoal())
    }

    fun updateGoal(goal: Goal) {
        appDatabase.goalDao().updateGoal(goal)
    }

    fun deleteGoal(goal: Goal) {
        appDatabase.goalDao().deleteGoal(goal)
    }
}
