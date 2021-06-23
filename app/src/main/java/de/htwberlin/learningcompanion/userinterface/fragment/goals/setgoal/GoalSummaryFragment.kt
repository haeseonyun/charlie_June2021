package de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal


import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal.nohelp.GoalNoHelpUserInputFragment
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_goal_no_help_user_input.*
import kotlinx.android.synthetic.main.fragment_goal_summary.*
import java.util.*

class GoalSummaryFragment : AbsGoalSettingFragment() {

    private lateinit var goal: Goal

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_goal_summary, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setClickListener()

        getGoal()
    }

    private fun setClickListener() {
        btn_yes.setOnClickListener(onYesButtonOnClickListener)
        btn_no.setOnClickListener(onNoButtonClickListener)
    }

    private fun getGoal() {
        goal = createGoalFromArguments()

        tv_goal_text.text = getGoalText(goal)
    }

    private val onNoButtonClickListener = fun(_:View) {
        btn_no.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private val onYesButtonOnClickListener = fun(_:View) {
        val goal = createGoalFromArguments()
        saveGoalToDatabase(goal)
        findNavController().navigate(R.id.action_global_mainScreenFragment)
    }

    private fun createGoalFromArguments(): Goal {
        return Goal(
                null,
                goalViewModel.editGoalHolder?.action!!,
                goalViewModel.editGoalHolder?.amount!!,
                goalViewModel.editGoalHolder?.field!!,
                goalViewModel.editGoalHolder?.medium!!,
                goalViewModel.editGoalHolder?.durationInMin!!.toInt(),
                goalViewModel.editGoalHolder?.untilTimeStamp,
                Calendar.getInstance().timeInMillis
        )
    }

    private fun saveGoalToDatabase(goal: Goal) {
        goalViewModel.repository.insertGoal(goal) {
            goalViewModel.currentGoal.value = it
        }
    }

    fun getGoalText(goal: Goal?): String {
        return goal?.run {
            when {
                untilTimeStamp != null -> "$action $amount $field $medium until $untilTimeStamp"
                durationInMin != null -> "$action $amount $field $medium for $durationInMin minutes"
                else -> "${action}, ${field}, ${medium}, ${amount}"
            }
        }!!
    }
}
