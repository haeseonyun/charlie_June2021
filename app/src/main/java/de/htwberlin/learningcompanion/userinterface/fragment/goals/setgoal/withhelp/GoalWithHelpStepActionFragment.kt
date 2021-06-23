package de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal.withhelp

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal.AbsGoalSettingFragment
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.hideKeyboard
import de.htwberlin.learningcompanion.util.setColorForString
import de.htwberlin.learningcompanion.util.setColorUsername
import kotlinx.android.synthetic.main.fragment_goal_with_help_step_action.*
import org.jetbrains.anko.support.v4.runOnUiThread

class GoalWithHelpStepActionFragment : AbsGoalSettingFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_goal_with_help_step_action, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        setClickListener()

        goalViewModel.editGoalHolder?.action?.let {
            et_action.setText(it)
        }


        setColorUsername(context!!, tv_goal_with_help_action_info1, "", getString(R.string.goal_with_help_action_info1))
    }

    private fun setClickListener() {
        btn_done.setOnClickListener(navigateToStepFieldFragmentWithValues)
    }

    private val navigateToStepFieldFragmentWithValues = fun(_:View) {
        if (validate(til_action, et_action)) {
            goalViewModel.editGoalHolder?.action = et_action.text.toString()

            findNavController().navigate(
                    GoalWithHelpStepActionFragmentDirections.actionGoalWithHelpStepActionFragmentToGoalWithHelpStepFieldFragment()
            )
        }
    }
}
