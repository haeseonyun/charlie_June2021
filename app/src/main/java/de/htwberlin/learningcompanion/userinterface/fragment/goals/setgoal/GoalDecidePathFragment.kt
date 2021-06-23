package de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.setColorForString
import kotlinx.android.synthetic.main.fragment_goal_decide_path.*

class GoalDecidePathFragment : Fragment() {

    private val viewModelFactory = ViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_goal_decide_path, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addClickListeners()

        val goalViewModel = ViewModelProviders.of(this, viewModelFactory).get(GoalViewModel::class.java)
        goalViewModel.editGoalHolder = GoalViewModel.EditGoal()


        setColorForString(tv_goal_decide_path_title, ContextCompat.getColor(context!!, R.color.colorAccent), "", SharedPreferencesHelper.get(context!!).getUserName(), getString(R.string.goal_decide_path_title))
    }

    private fun addClickListeners(){
        btn_no.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_goalDecidePathFragment_to_goalWithHelpInfoFragment))
        btn_yes.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_goalDecidePathFragment_to_goalNoHelpInfoFragment))
    }

}
