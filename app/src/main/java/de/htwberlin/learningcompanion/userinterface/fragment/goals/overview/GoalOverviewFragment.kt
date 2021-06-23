package de.htwberlin.learningcompanion.userinterface.fragment.goals.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.userinterface.adapter.GenericRecyclerAdapter
import de.htwberlin.learningcompanion.userinterface.adapter.WidgetData
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.userinterface.widget.goaloverview.GoalOverviewItemData
import de.htwberlin.learningcompanion.userinterface.widget.goaloverview.GoalOverviewItemView
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.setColorForString
import kotlinx.android.synthetic.main.fragment_goal_overview.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton


class GoalOverviewFragment : Fragment(), AdapterView.OnItemClickListener {

    private val goalViewModel: GoalViewModel by activityViewModels(factoryProducer = { ViewModelFactory })

    private lateinit var genericRecyclerAdapter: GenericRecyclerAdapter<WidgetData>
    private val goalList = ArrayList<GoalOverviewItemData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_goal_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        goalViewModel.currentGoal.observe(viewLifecycleOwner, currentGoalObserver)
        goalViewModel.goals.observe(viewLifecycleOwner, goalOverviewObserver )

        updateHeaderLayout()

        genericRecyclerAdapter = GenericRecyclerAdapter(GoalOverviewItemView::class.java)
        genericRecyclerAdapter.onItemClickListener = this
        genericRecyclerAdapter.data = goalList

        rv_goals.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = genericRecyclerAdapter
        }

        btn_new_goal.setOnClickListener(Navigation.createNavigateOnClickListener(
                GoalOverviewFragmentDirections.actionGoalOverviewFragmentToNavGraph()
        ))

        btn_goal_edit.setOnClickListener(navigateToEditGoal)
        btn_delete_goal.setOnClickListener(openDeleteDialog)
    }

    private val goalOverviewObserver = Observer<List<Goal>> { goals ->
        goalList.clear()
        goals.forEach {
            goalList.add(GoalOverviewItemData(it, goalViewModel.currentGoal.value == it))
        }
        genericRecyclerAdapter.data = goalList
        updateHeaderLayout()
    }

    private val currentGoalObserver = Observer<Goal?> {currentGoal ->
        if (currentGoal != null) {
            goalList.forEach {
                it.isCurrentGoal = it.data == currentGoal
            }
            genericRecyclerAdapter.notifyDataSetChanged()
        }
    }

    private val openDeleteDialog = fun(_:View) {
        alert("Do you REALLY want to delete the goal?", "Delete goal") {
            yesButton {
                goalViewModel.deleteGoal()
                updateHeaderLayout()
            }
            noButton {
                toast("Good :)")
            }
        }.show()
    }

    private fun updateHeaderLayout() {
        iv_charlie.setImageDrawable(BuddyFaceHolder.get(requireContext()).getDefaultFace())

        if (goalViewModel.goals.value?.isEmpty() == true) {
            setColorForString(tv_charlie_info, ContextCompat.getColor(requireContext(), R.color.colorAccent), "", SharedPreferencesHelper.get(requireContext()).getUserName(), ", you don't have added any goals yet. \nYou can add new goals by clicking on the \"Add\"-Button.")
        } else {

            if (goalViewModel.currentGoal.value != null) {
                setColorForString(tv_charlie_info, ContextCompat.getColor(requireContext(), R.color.colorAccent), "", SharedPreferencesHelper.get(requireContext()).getUserName(), ", your currently selected goal is: \n${getGoalText(goalViewModel.currentGoal.value)}")
            } else {
                setColorForString(tv_charlie_info, ContextCompat.getColor(requireContext(), R.color.colorAccent), "", SharedPreferencesHelper.get(requireContext()).getUserName(), ", you do not have a goal selected. \nYou can select one below or create a completely new goal.")
            }
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

    private val navigateToEditGoal = fun(_:View) {
        goalViewModel.currentGoal.value?.let {
            findNavController().navigate(
                    GoalOverviewFragmentDirections.actionGoalOverviewFragmentToGoalEditFragment(it.id!!)
            )
        }
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        goalViewModel.currentGoal.value = goalList[position].data

        updateHeaderLayout()
    }

}
