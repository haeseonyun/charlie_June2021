package de.htwberlin.learningcompanion.userinterface.widget.session

import android.view.View
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.LearningSession
import de.htwberlin.learningcompanion.persistance.model.Place
import de.htwberlin.learningcompanion.userinterface.adapter.WidgetData

/**
 * Created by Max on 2019-06-01.
 *
 * @author Max Oehme
 */
class SessionListItemData(
        val data: LearningSession,
        val goal: Goal,
        val place: Place
) : WidgetData {
    override var id: Long = -1L
    override val viewType: Class<out View> = SessionListItemView::class.java
}