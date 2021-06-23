package de.htwberlin.learningcompanion.userinterface.widget.goaloverview

import android.view.View
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.userinterface.adapter.WidgetData

/**
 * Created by Max on 2019-06-01.
 *
 * @author Max Oehme
 */
class GoalOverviewItemData(
        val data: Goal,
        var isCurrentGoal: Boolean
) : WidgetData {
    override var id: Long = -1L
    override val viewType: Class<out View> = GoalOverviewItemView::class.java
}