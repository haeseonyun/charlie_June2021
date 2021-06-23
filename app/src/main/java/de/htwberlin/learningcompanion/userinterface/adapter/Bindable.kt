package de.htwberlin.learningcompanion.userinterface.adapter

/**
 * Created by Max on 13.12.18.
 *
 * @author Max Oehme
 */
interface Bindable<in T> {
    fun onBind(data: T)
}