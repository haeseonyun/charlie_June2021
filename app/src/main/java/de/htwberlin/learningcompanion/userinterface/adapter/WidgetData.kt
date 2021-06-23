package de.htwberlin.learningcompanion.userinterface.adapter

import android.view.View

/**
 * Created by Max on 20.12.18.
 *
 * @author Max Oehme
 */
interface WidgetData {
    val id: Long
    val viewType: Class<out View>
}