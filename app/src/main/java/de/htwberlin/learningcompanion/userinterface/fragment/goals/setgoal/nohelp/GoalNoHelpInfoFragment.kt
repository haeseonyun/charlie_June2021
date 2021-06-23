package de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal.nohelp


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.htwberlin.learningcompanion.R
import kotlinx.android.synthetic.main.fragment_goal_no_help_info.*


class GoalNoHelpInfoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_goal_no_help_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addLayoutClickListener()
    }

    private fun addLayoutClickListener() {
        cl_goal_no_help_info.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_goalNoHelpInfoFragment_to_goalNoHelpUserInputFragment, null))
    }
}
