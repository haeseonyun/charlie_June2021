package de.htwberlin.learningcompanion.userinterface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.repository.GoalRepository

/**
 * Created by Max on 2019-06-02.
 *
 * @author Max Oehme
 */
class GoalViewModel(
        val repository: GoalRepository
): ViewModel() {

    var editGoalHolder: EditGoal? = null

    data class EditGoal(
            var action: String? = null,
            var amount: String? = null,
            var field: String? = null,
            var medium: String? = null,
            var durationInMin: String? = null,
            var untilTimeStamp: String? = null
    )

    val currentGoal: MutableLiveData<Goal?> by lazy {
        MutableLiveData<Goal?>()
    }

    val goals: LiveData<List<Goal>> by lazy {
        repository.getAllGoalsLiveData()
    }

    fun deleteGoal() {
        currentGoal.value?.let {
            repository.deleteGoal(it)
        }
        currentGoal.value = null
    }

}