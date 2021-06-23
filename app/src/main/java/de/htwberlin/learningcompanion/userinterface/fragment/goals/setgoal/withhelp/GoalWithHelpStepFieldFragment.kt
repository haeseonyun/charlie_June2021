package de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal.withhelp

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal.AbsGoalSettingFragment
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.setColorForString
import kotlinx.android.synthetic.main.fragment_goal_with_help_step_field.*
import org.jetbrains.anko.support.v4.runOnUiThread


class GoalWithHelpStepFieldFragment : AbsGoalSettingFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_goal_with_help_step_field, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setClickListener()

        goalViewModel.editGoalHolder?.field?.let {
            et_field.setText(it)
        }
        setColorForString(tv_goal_with_help_field_info1, ContextCompat.getColor(context!!, R.color.colorAccent), "", SharedPreferencesHelper.get(context!!).getUserName(), getString(R.string.goal_with_help_field_info1))
    }

    private fun setClickListener() {
        btn_done.setOnClickListener(navigateNext)
        btn_back.setOnClickListener(navigatePrevious)
    }

    private val navigatePrevious = fun(_:View) {
        goalViewModel.editGoalHolder?.field = et_field.text.toString()

        findNavController().popBackStack()
    }

    private val navigateNext = fun(_:View) {
        if (validate(til_field, et_field)) {
            goalViewModel.editGoalHolder?.field = et_field.text.toString()

            findNavController().navigate(
                    GoalWithHelpStepFieldFragmentDirections.actionGoalWithHelpStepFieldFragmentToGoalWithHelpStepObjectFragment()
            )
        }
    }

}
