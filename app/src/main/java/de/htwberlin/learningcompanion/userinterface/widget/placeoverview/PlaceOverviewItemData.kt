package de.htwberlin.learningcompanion.userinterface.widget.placeoverview

import android.view.View
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.Place
import de.htwberlin.learningcompanion.userinterface.adapter.WidgetData

/**
 * Created by Max on 2019-06-01.
 *
 * @author Max Oehme
 */
class PlaceOverviewItemData(
        val data: Place,
        var isCurrentPlace: Boolean
) : WidgetData {
    override var id: Long = -1L
    override val viewType: Class<out View> = PlaceOverviewItemView::class.java
}