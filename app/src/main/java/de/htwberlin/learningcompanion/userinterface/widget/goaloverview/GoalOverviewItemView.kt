package de.htwberlin.learningcompanion.userinterface.widget.goaloverview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.userinterface.adapter.Bindable
import kotlinx.android.synthetic.main.goal_list_item.view.*

/**
 * Created by Max on 2019-06-01.
 *
 * @author Max Oehme
 */
class GoalOverviewItemView(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
): LinearLayout(context, attrs, defStyleAttr, defStyleRes), Bindable<GoalOverviewItemData> {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        View.inflate(context, R.layout.goal_list_item, this)

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    override fun onBind(data: GoalOverviewItemData) {

        data.data.let { goal ->
            tv_action.text = goal.action
            tv_amount.text = goal.amount
            tv_medium.text = goal.medium
            tv_field.text = goal.field
            tv_duration.text = getGoalDurationText(goal)

            cb_set_current_goal.isChecked = data.isCurrentGoal
            cb_set_current_goal.setOnClickListener {
                performClick()
            }
        }

    }

    private fun getGoalDurationText(goal: Goal): String {
        return if (goal.durationInMin != null) {
            "${goal.durationInMin} minutes"
        } else {
            "${goal.untilTimeStamp}"
        }
    }
}